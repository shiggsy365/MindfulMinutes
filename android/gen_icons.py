import struct, zlib, math, os

def make_png(size):
    def make_chunk(chunk_type, data):
        c = chunk_type + data
        return struct.pack('>I', len(data)) + c + struct.pack('>I', zlib.crc32(c) & 0xFFFFFFFF)
    header = b'\x89PNG\r\n\x1a\n'
    ihdr_data = struct.pack('>IIBBBBB', size, size, 8, 2, 0, 0, 0)
    ihdr = make_chunk(b'IHDR', ihdr_data)
    cx = cy = size / 2
    radius = size * 0.45
    inner = size * 0.2
    raw = []
    for y in range(size):
        row = bytearray([0])
        for x in range(size):
            dist = math.sqrt((x-cx)**2 + (y-cy)**2)
            if dist <= inner:
                row.extend([10, 15, 12])
            elif dist <= radius:
                row.extend([167, 199, 168])
            else:
                row.extend([10, 15, 12])
        raw.append(bytes(row))
    idat = make_chunk(b'IDAT', zlib.compress(b''.join(raw), 9))
    return header + ihdr + idat + make_chunk(b'IEND', b'')

base = os.path.join(os.path.dirname(__file__), 'app/src/main/res')
sizes = [('mipmap-mdpi',48),('mipmap-hdpi',72),('mipmap-xhdpi',96),('mipmap-xxhdpi',144),('mipmap-xxxhdpi',192)]
for folder, size in sizes:
    d = os.path.join(base, folder)
    os.makedirs(d, exist_ok=True)
    png = make_png(size)
    for name in ['ic_launcher.png', 'ic_launcher_round.png']:
        with open(os.path.join(d, name), 'wb') as f:
            f.write(png)
    print(f'{folder} {size}px OK')
print('Icons done.')
