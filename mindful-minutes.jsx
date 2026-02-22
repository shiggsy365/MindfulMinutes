import { useState, useEffect, useCallback, useRef } from "react";

// ============================================================
// HELPERS
// ============================================================
function dk(d){return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,"0")}-${String(d.getDate()).padStart(2,"0")}`}
function dn(d){return ["Sun","Mon","Tue","Wed","Thu","Fri","Sat"][d.getDay()]}
function mn(d){return ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"][d.getMonth()]}
const FS="'Cormorant Garamond', serif", FK="'Karla', sans-serif";

// ============================================================
// EXERCISE DATA - 60 exercises, 6 categories
// ============================================================
const CATEGORIES=[
  {id:"breathing",name:"Breathing",icon:"🌬️",color:"rgba(130,190,200,0.9)",bg:"rgba(130,190,200,0.08)",border:"rgba(130,190,200,0.2)",desc:"Anchor yourself through the rhythm of your breath"},
  {id:"body",name:"Body Scan",icon:"🧘",color:"rgba(180,160,210,0.9)",bg:"rgba(180,160,210,0.08)",border:"rgba(180,160,210,0.2)",desc:"Tune into the sensations held within your body"},
  {id:"senses",name:"Senses",icon:"👁️",color:"rgba(200,180,140,0.9)",bg:"rgba(200,180,140,0.08)",border:"rgba(200,180,140,0.2)",desc:"Awaken awareness through sight, sound, and touch"},
  {id:"gratitude",name:"Gratitude",icon:"🌿",color:"rgba(150,195,150,0.9)",bg:"rgba(150,195,150,0.08)",border:"rgba(150,195,150,0.2)",desc:"Cultivate appreciation for the present moment"},
  {id:"movement",name:"Mindful Movement",icon:"🌊",color:"rgba(140,175,210,0.9)",bg:"rgba(140,175,210,0.08)",border:"rgba(140,175,210,0.2)",desc:"Find stillness through gentle, intentional motion"},
  {id:"visualisation",name:"Visualisation",icon:"✨",color:"rgba(210,170,180,0.9)",bg:"rgba(210,170,180,0.08)",border:"rgba(210,170,180,0.2)",desc:"Journey inward through the landscape of imagination"},
];

const EXERCISES=[
  {id:"b1",cat:"breathing",name:"Box Breathing",mins:3,steps:["Breathe in slowly through your nose for 4 counts.","Hold your breath gently for 4 counts.","Exhale slowly through your mouth for 4 counts.","Hold empty for 4 counts. Repeat the cycle.","With each round, feel your heartbeat slow and your mind settle."]},
  {id:"b2",cat:"breathing",name:"4-7-8 Calm",mins:2,steps:["Place the tip of your tongue behind your upper front teeth.","Inhale quietly through your nose for 4 counts.","Hold your breath for 7 counts.","Exhale completely through your mouth for 8 counts, making a whoosh sound.","Repeat three more times, letting each exhale release more tension."]},
  {id:"b3",cat:"breathing",name:"Ocean Breath",mins:3,steps:["Slightly constrict the back of your throat.","Inhale deeply through your nose, creating a soft ocean-like sound.","Exhale slowly through your nose with the same gentle constriction.","Let the rhythm of your breath become the rhythm of waves.","Continue for several minutes, riding each wave of breath."]},
  {id:"b4",cat:"breathing",name:"Belly Breathing",mins:2,steps:["Place one hand on your chest and one on your belly.","Breathe in deeply so only your belly hand rises.","Exhale and feel your belly gently fall.","Keep your chest hand as still as possible.","Continue, feeling the gentle rise and fall like a sleeping child."]},
  {id:"b5",cat:"breathing",name:"Alternate Nostril",mins:4,steps:["Close your right nostril with your thumb and inhale through the left.","Close the left nostril with your ring finger, release the right.","Exhale through the right nostril slowly.","Inhale through the right, then switch and exhale through the left.","Continue alternating, feeling balance return to your mind."]},
  {id:"b6",cat:"breathing",name:"Counted Exhale",mins:2,steps:["Inhale naturally without counting.","Exhale slowly, counting each second: one... two... three...","Try to extend your exhale a little longer each time.","Aim for an exhale that's twice as long as your inhale.","Feel your nervous system shift toward calm with each breath."]},
  {id:"b7",cat:"breathing",name:"Straw Breathing",mins:1,steps:["Inhale deeply through your nose.","Purse your lips as if breathing through a tiny straw.","Exhale as slowly as possible through your pursed lips.","Feel the controlled release of air grounding you.","Repeat five times, each exhale longer than the last."]},
  {id:"b8",cat:"breathing",name:"Energising Breath",mins:2,steps:["Sit tall and take three normal breaths to settle.","Begin quick, rhythmic breaths through your nose — equal inhale and exhale.","Keep breaths short and pumping from the diaphragm for 15 seconds.","Stop and take one long, deep breath in. Hold for 5 counts.","Exhale slowly. Notice the tingling aliveness in your body."]},
  {id:"b9",cat:"breathing",name:"Sighing Release",mins:1,steps:["Inhale deeply through your nose, filling your lungs completely.","Let out an audible sigh — a big, dramatic exhale through your mouth.","Feel the release of tension with the sound.","Inhale again deeply. Sigh it out even louder.","Three more sighs, each one letting go of something you're carrying."]},
  {id:"b10",cat:"breathing",name:"Candle Breathing",mins:2,steps:["Imagine a candle flame at arm's length in front of you.","Inhale deeply through your nose.","Exhale so gently that you would only make the flame flicker, not go out.","Focus entirely on the smoothness and control of your exhale.","Continue for ten breaths, each softer than the last."]},
  {id:"bs1",cat:"body",name:"Head to Toe Scan",mins:5,steps:["Close your eyes and bring attention to the crown of your head.","Slowly move your awareness down: forehead, eyes, jaw — releasing tension.","Continue through your neck, shoulders, arms, and fingertips.","Scan through your chest, belly, hips, and lower back.","Flow down through your legs to the tips of your toes. Breathe."]},
  {id:"bs2",cat:"body",name:"Tension Spotter",mins:3,steps:["Take three slow breaths to settle into stillness.","Scan your body and find the place holding the most tension.","Breathe directly into that area — imagine warmth flowing in.","With each exhale, picture the tension dissolving like mist.","When it softens, scan for the next spot. Repeat until at ease."]},
  {id:"bs3",cat:"body",name:"Warm Light Scan",mins:4,steps:["Imagine a warm, golden light resting above your head.","As you inhale, draw this light down into your scalp and face.","With each breath, let it flow further — neck, chest, arms.","Feel its warmth relaxing every muscle it touches.","Let it fill your entire body until you glow from within."]},
  {id:"bs4",cat:"body",name:"Hands Awareness",mins:2,steps:["Rest your hands palms-up on your knees.","Bring all attention to your left hand. Feel its weight.","Notice temperature, tingling, the pulse in your fingertips.","Shift attention to your right hand. Observe without judging.","Hold awareness of both hands at once. Feel them alive."]},
  {id:"bs5",cat:"body",name:"Grounding Feet",mins:2,steps:["Press your feet flat on the ground.","Feel the floor — its temperature, texture, firmness.","Imagine roots from your soles deep into the earth.","With each breath, feel more anchored.","Wiggle your toes. You are here. You are grounded."]},
  {id:"bs6",cat:"body",name:"Jaw & Face Release",mins:2,steps:["Unclench your jaw. Let your mouth fall slightly open.","Soften the muscles around your eyes.","Relax your forehead, smoothing away furrows.","Let your tongue rest softly at the bottom of your mouth.","Notice how much tension your face was holding."]},
  {id:"bs7",cat:"body",name:"Progressive Relaxation",mins:5,steps:["Curl your toes tightly 5 seconds, then release.","Tense calves and thighs 5 seconds. Let go.","Squeeze fists, tighten arms. Hold. Release.","Scrunch shoulders to ears. Hold. Drop them.","Tense your whole face. Hold. Release. Deeply relaxed."]},
  {id:"bs8",cat:"body",name:"Heartbeat Meditation",mins:3,steps:["Place your hand over your heart.","Feel the steady rhythm beneath your palm.","With each beat, silently say 'here.'","If your mind wanders, return to the beat.","Feel gratitude for this tireless rhythm."]},
  {id:"bs9",cat:"body",name:"Spine Check-In",mins:2,steps:["Notice your posture without correcting it.","From tailbone, scan upward along your spine.","Notice curves, tension, or discomfort.","Imagine a thread pulling up from the crown.","Feel the quiet dignity of an upright spine."]},
  {id:"bs10",cat:"body",name:"Breath in the Body",mins:3,steps:["Breathe normally. Where do you feel it most?","Nostrils? Chest? Belly?","Follow the air inward — feel lungs expand.","Follow it out — the gentle release.","Witness breath moving through you like a visitor."]},
  {id:"s1",cat:"senses",name:"5-4-3-2-1 Grounding",mins:3,steps:["Name 5 things you can see. Really look.","Name 4 things you can touch. Feel textures.","Name 3 things you can hear. Even quiet ones.","Name 2 things you can smell. Breathe deeply.","Name 1 thing you can taste. Let it linger."]},
  {id:"s2",cat:"senses",name:"Deep Listening",mins:3,steps:["Close your eyes. Become still.","Listen for the farthest sound.","Find the closest, most subtle sound.","Hold awareness of distant and near.","Let sounds wash over you. Just listen."]},
  {id:"s3",cat:"senses",name:"Texture Explorer",mins:2,steps:["Find a nearby object.","Close eyes. Explore with fingertips.","Notice ridges, temperature, surface quality.","Move slowly. As if for the first time.","Open eyes. See it with fresh appreciation."]},
  {id:"s4",cat:"senses",name:"Colour Seeking",mins:2,steps:["Choose a colour that calls to you.","Find every instance around you.","Notice shades you'd normally overlook.","How does this change your perception?","Appreciate the richness surrounding you."]},
  {id:"s5",cat:"senses",name:"Mindful Sip",mins:2,steps:["Take a drink nearby.","Feel the weight and temperature of the cup.","Bring it to your lips slowly.","One small sip. Hold it. Notice every flavour.","Swallow slowly. One sip, fully lived."]},
  {id:"s6",cat:"senses",name:"Skygazing",mins:3,steps:["Look up at the sky.","Notice colours: blues, greys, whites, golds.","Watch clouds drift without destination.","Let thoughts be like clouds — passing through.","Feel the vast openness. Small and peaceful."]},
  {id:"s7",cat:"senses",name:"Sound Bath",mins:4,steps:["Sit with ambient sounds.","Let all sounds arrive without filtering.","Isolate one sound. Follow its rhythm.","Release it. Pick another.","Let all sounds merge into a symphony."]},
  {id:"s8",cat:"senses",name:"Barefoot Moment",mins:2,steps:["Remove shoes if you can.","Place bare feet on the ground.","Notice temperature, texture, pressure.","Shift weight slowly from heel to toe.","The earth supports you completely."]},
  {id:"s9",cat:"senses",name:"Scent Journey",mins:2,steps:["Find something with a scent.","Hold near your nose. Inhale slowly.","Notice layers. Does it change?","Let it trigger memories. Just notice.","Appreciate this invisible, powerful sense."]},
  {id:"s10",cat:"senses",name:"Peripheral Vision",mins:1,steps:["Fix gaze on a single point ahead.","Expand awareness to the edges.","Notice shapes, colours, movement.","Hold this wide, soft focus 30 seconds.","Panoramic awareness shifts you out of stress."]},
  {id:"g1",cat:"gratitude",name:"Three Good Things",mins:2,steps:["Three good things from today.","They can be tiny — warmth, kindness, sunlight.","Sit with each feeling for a few breaths.","Whisper 'thank you' for each one.","Carry this warmth forward."]},
  {id:"g2",cat:"gratitude",name:"Body Gratitude",mins:3,steps:["Thank your hands for everything they do.","Thank your legs for carrying you.","Thank your eyes for beauty they show.","Thank your lungs for every breath.","Your body works tirelessly. Appreciate it."]},
  {id:"g3",cat:"gratitude",name:"Person Appreciation",mins:3,steps:["Someone who positively impacted your life.","Picture their face. A specific moment.","Feel gratitude as warmth in your chest.","Send them: 'May you be happy.'","Consider telling them how you feel."]},
  {id:"g4",cat:"gratitude",name:"Ordinary Miracles",mins:2,steps:["Find something utterly ordinary.","Consider its journey to be here.","People, materials, effort involved.","Let wonder fill you at the invisible web.","The ordinary is extraordinary."]},
  {id:"g5",cat:"gratitude",name:"Gratitude Letter",mins:5,steps:["Someone you've never properly thanked.","Compose a short letter mentally.","What they did. How it affected you.","Feel emotion with each mental line.","The feeling is enough for now."]},
  {id:"g6",cat:"gratitude",name:"Senses Gratitude",mins:2,steps:["Thank eyes for one beautiful thing.","Thank ears for one comforting sound.","Thank skin for one soothing touch.","Thank nose for one alive scent.","Thank tongue for one joyful taste."]},
  {id:"g7",cat:"gratitude",name:"Past Self Thanks",mins:3,steps:["A difficult time you survived.","Thank your past self for enduring.","Acknowledge the strength it took.","Recognise how it shaped you.","You're here because you kept going."]},
  {id:"g8",cat:"gratitude",name:"Comfort Inventory",mins:2,steps:["Notice comfort you're experiencing now.","Roof. Clothing. Air in your lungs.","Not guaranteed for everyone.","Feel genuine appreciation.","Consider sharing comfort with others."]},
  {id:"g9",cat:"gratitude",name:"Future Gratitude",mins:2,steps:["Imagine yourself one year from now, at peace.","They look back at today with gratitude.","What seeds are you planting now?","Feel the connection between today and tomorrow.","Trust that what you're building matters."]},
  {id:"g10",cat:"gratitude",name:"Meal Blessing",mins:1,steps:["Pause before eating. Look at the food.","Think of sun, rain, soil that grew it.","Think of hands that prepared it.","Silent gratitude for nourishment.","First bite slowly, tasting gratitude."]},
  {id:"m1",cat:"movement",name:"Gentle Neck Rolls",mins:2,steps:["Drop chin to chest. Feel the stretch.","Roll head to the right slowly.","Continue back, then left. Full circle.","Incredibly slowly — every micro-sensation.","Reverse. Three circles each way."]},
  {id:"m2",cat:"movement",name:"Standing Mountain",mins:3,steps:["Feet hip-width. Feel the ground.","Press all four corners of each foot.","Stack hips, shoulders, head upward.","Crown reaches toward sky. Arms at sides.","Still, strong, unmoved by thoughts."]},
  {id:"m3",cat:"movement",name:"Shoulder Waterfall",mins:2,steps:["Inhale, shoulders up to ears.","Hold high and tight 3 seconds.","Exhale, let them drop — waterfall.","Feel tension versus release.","Five times. Each drop washes stress."]},
  {id:"m4",cat:"movement",name:"Mindful Walking",mins:5,steps:["Stand still. Weight on your feet.","Begin walking very slowly.","Notice weight shift, leg swing, placement.","Each step deliberate. Nowhere else to be.","Heel, ball, toe. Heel, ball, toe."]},
  {id:"m5",cat:"movement",name:"Cat-Cow Stretch",mins:2,steps:["Hands and knees position.","Inhale: belly drops, chin lifts.","Exhale: round spine, tuck chin.","Flow between shapes with breath.","Spine liquid, moving like a wave."]},
  {id:"m6",cat:"movement",name:"Finger Tap Rhythm",mins:1,steps:["Hands on a flat surface.","Tap each finger to thumb in sequence.","Forward and backward, full attention.","Speed up, then slow down.","Simple rhythm, out of head, into hands."]},
  {id:"m7",cat:"movement",name:"Ragdoll Fold",mins:2,steps:["Stand hip-width. Deep breath in.","Fold forward, arms and head hang.","Grab opposite elbows. Sway gently.","Let gravity do the work.","Slowly roll up, one vertebra at a time."]},
  {id:"m8",cat:"movement",name:"Wrist & Hand Release",mins:2,steps:["Extend arms. Make fists. Squeeze 3 sec.","Release. Spread fingers wide.","Rotate wrists — five circles each way.","Shake hands loosely, flicking water.","Palms together. Breathe. Be grateful."]},
  {id:"m9",cat:"movement",name:"Slow Stretch Reach",mins:2,steps:["Inhale, arms overhead.","Reach high. Whole body lengthens.","Lean right. Two breaths.","Lean left. Two breaths.","Lower slowly. Feel the space you created."]},
  {id:"m10",cat:"movement",name:"Seated Twist",mins:2,steps:["Sit tall, feet flat.","Right hand on left knee.","Inhale lengthen, exhale twist left.","Hold three breaths, look over shoulder.","Return to centre. Repeat other side."]},
  {id:"v1",cat:"visualisation",name:"Safe Place",mins:4,steps:["Imagine where you feel completely safe.","Build it: colours, light, details.","What sounds? What temperature?","Place yourself in the centre. Breathe.","This place is always within you."]},
  {id:"v2",cat:"visualisation",name:"Floating Leaf",mins:3,steps:["A slow, clear stream in a peaceful forest.","Each thought — place it on a leaf.","Watch it float downstream and away.","Don't chase it. Another will come.","The stream always flows."]},
  {id:"v3",cat:"visualisation",name:"Inner Garden",mins:5,steps:["Step into a beautiful garden.","Your garden — designed by your deepest peace.","Walk the paths. What blooms? What stands tall?","Find a bench. Sit. Birds. Warm light.","Plant a seed of intention before you leave."]},
  {id:"v4",cat:"visualisation",name:"Colour Breathing",mins:2,steps:["Choose a colour that means calm.","Inhale it as a soft light.","Feel it fill lungs, spread through body.","Exhale murky grey — your stress.","Inhale colour. Exhale grey. Glow with calm."]},
  {id:"v5",cat:"visualisation",name:"Mountain Meditation",mins:4,steps:["A great mountain — solid, majestic.","Seasons pass: snow, rain, sun, wind.","The mountain remains. Still. Complete.","You are this mountain. Emotions are weather.","Sit with inner strength and permanence."]},
  {id:"v6",cat:"visualisation",name:"Ocean of Calm",mins:3,steps:["Standing at a vast, calm ocean shore.","Warm, gentle. Waves lap softly.","Wade in slowly. Feel water support you.","Float weightless. Infinite blue sky.","Each wave rocks you deeper into peace."]},
  {id:"v7",cat:"visualisation",name:"Starlight Shower",mins:3,steps:["Lying on soft grass under night sky.","Stars fall softly like glowing snow.","Each one dissolves a worry on landing.","Forehead, chest, arms, legs.","Covered in starlight. Light as air. Free."]},
  {id:"v8",cat:"visualisation",name:"Letting Go Balloon",mins:2,steps:["Something weighing on your mind.","Write it on a tag. Tie to a balloon.","Choose the colour. Hold the string.","Deep breath. Release the string.","Watch it rise and disappear. Breathe."]},
  {id:"v9",cat:"visualisation",name:"Warm Cocoon",mins:3,steps:["A soft, warm cocoon around your body.","Perfect weight — a hug from all directions.","The world is muffled. Only warmth.","Muscles surrender. Mind quiets.","You are held. You are safe."]},
  {id:"v10",cat:"visualisation",name:"Sunrise Within",mins:3,steps:["Golden light in the centre of your chest.","With each breath, brighter and warmer.","Expanding outward — filling your whole body.","Darkness transforms to golden warmth.","You carry your own light. You always have."]},
];

// ============================================================
// BREATHING PATTERNS
// ============================================================
function getBreathingPattern(ex){const n=ex.name.toLowerCase();
if(n.includes("box"))return{inhale:4,hold1:4,exhale:4,hold2:4,label:"Box",ins:{inhale:"Breathe in slowly through your nose…",hold1:"Hold gently…",exhale:"Exhale slowly through your mouth…",hold2:"Hold empty — feel the stillness…"}};
if(n.includes("4-7-8"))return{inhale:4,hold1:7,exhale:8,hold2:0,label:"4-7-8",ins:{inhale:"Inhale quietly through your nose…",hold1:"Hold — stay relaxed…",exhale:"Exhale completely with a whoosh…",hold2:""}};
if(n.includes("ocean"))return{inhale:5,hold1:0,exhale:5,hold2:0,label:"Ocean",ins:{inhale:"Inhale deeply, soft ocean sound…",hold1:"",exhale:"Exhale slowly, gentle constriction…",hold2:""}};
if(n.includes("belly"))return{inhale:5,hold1:0,exhale:5,hold2:0,label:"Belly",ins:{inhale:"Breathe in — feel your belly rise…",hold1:"",exhale:"Exhale — belly gently falls…",hold2:""}};
if(n.includes("alternate"))return{inhale:4,hold1:2,exhale:4,hold2:2,label:"Alternate",ins:{inhale:"Inhale through one nostril…",hold1:"Pause — switch…",exhale:"Exhale through the other…",hold2:"Pause before switching…"}};
if(n.includes("counted"))return{inhale:3,hold1:0,exhale:6,hold2:0,label:"Counted",ins:{inhale:"Inhale naturally…",hold1:"",exhale:"Exhale slowly, counting each second…",hold2:""}};
if(n.includes("straw"))return{inhale:3,hold1:0,exhale:7,hold2:0,label:"Straw",ins:{inhale:"Inhale deeply through your nose…",hold1:"",exhale:"Exhale through pursed lips…",hold2:""}};
if(n.includes("energi"))return{inhale:1,hold1:0,exhale:1,hold2:0,label:"Rhythmic",ins:{inhale:"Quick breath in…",hold1:"",exhale:"Quick breath out…",hold2:""}};
if(n.includes("sigh"))return{inhale:4,hold1:0,exhale:6,hold2:0,label:"Sighing",ins:{inhale:"Inhale deeply, filling lungs…",hold1:"",exhale:"Let out an audible sigh…",hold2:""}};
if(n.includes("candle"))return{inhale:4,hold1:0,exhale:7,hold2:0,label:"Candle",ins:{inhale:"Inhale deeply…",hold1:"",exhale:"Exhale gently — flame just flickers…",hold2:""}};
return{inhale:4,hold1:2,exhale:6,hold2:0,label:"Calm",ins:{inhale:"Breathe in slowly…",hold1:"Hold gently…",exhale:"Exhale slowly…",hold2:""}};}

function getBreathPhase(p,el){const c=p.inhale+p.hold1+p.exhale+p.hold2,pos=el%c;
if(pos<p.inhale)return{phase:"inhale",countdown:Math.ceil(p.inhale-pos),progress:pos/p.inhale};
if(p.hold1>0&&pos<p.inhale+p.hold1)return{phase:"hold1",countdown:Math.ceil(p.hold1-(pos-p.inhale)),progress:(pos-p.inhale)/p.hold1};
if(pos<p.inhale+p.hold1+p.exhale)return{phase:"exhale",countdown:Math.ceil(p.exhale-(pos-p.inhale-p.hold1)),progress:(pos-p.inhale-p.hold1)/p.exhale};
return{phase:"hold2",countdown:Math.ceil(p.hold2-(pos-p.inhale-p.hold1-p.exhale)),progress:(pos-p.inhale-p.hold1-p.exhale)/p.hold2};}

function BreathingGuide({pattern,elapsed}){const{phase,progress,countdown}=getBreathPhase(pattern,elapsed);
const scale=phase==="inhale"?1+progress*0.45:phase==="exhale"?1.45-progress*0.45:phase==="hold1"?1.45:1;
const cs={inhale:"rgba(150,200,170,",exhale:"rgba(150,170,200,",hold1:"rgba(200,190,150,",hold2:"rgba(200,190,150,"};const c=cs[phase];
const lbl={inhale:"↑ Inhale",exhale:"↓ Exhale",hold1:"· Hold ·",hold2:"· Hold ·"};
return(<div style={{display:"flex",flexDirection:"column",alignItems:"center",gap:"0.6rem",marginBottom:"1.25rem"}}>
<div style={{width:80,height:80,borderRadius:"50%",background:c+"0.12)",border:`1.5px solid ${c}0.35)`,display:"flex",alignItems:"center",justifyContent:"center",transform:`scale(${scale})`,transition:"transform 1s ease, background 0.5s ease",opacity:0.7}}>
<span style={{fontFamily:FK,fontSize:"1.5rem",fontWeight:300,color:"rgba(255,255,255,0.7)"}}>{countdown}</span></div>
<span style={{fontFamily:FK,fontSize:"0.9rem",fontWeight:400,color:c+"0.9)",letterSpacing:"0.15em",textTransform:"uppercase",transition:"color 0.5s ease"}}>{lbl[phase]}</span></div>);}

// ============================================================
// MOOD DATA
// ============================================================
const MOODS=[
  {id:"anxious",name:"Anxious",emoji:"😰",color:"rgba(130,175,210,0.9)",bg:"rgba(130,175,210,0.08)",border:"rgba(130,175,210,0.2)",message:"Anxiety is just energy looking for direction.",sessions:[
    {id:"anx-q",length:"quick",label:"Quick Calm",mins:2,steps:["Place both feet flat on the ground.","Inhale 4, hold 4, exhale 6. Repeat 3x.","Name 5 things you can see.","Feel body weight. You are anchored.","'This feeling is temporary. I am safe.'"]},
    {id:"anx-m",length:"medium",label:"Worry Unwinding",mins:5,steps:["Anxious thoughts as tangled threads.","Choose the loudest worry.","Is it about now or the future?","Breathe in calm, breathe out the thread.","Pick the next. Examine, release.","Continue until the tangle loosens.","Hand on chest. Feel it slow.","Worries are lighter now."]},
    {id:"anx-d",length:"deep",label:"Safe Harbour",mins:10,steps:["Settle in. Body heavy.","10 slow breaths, exhale twice as long.","A peaceful harbour entrance.","Still water. Boats rock gently.","Walk the harbour wall.","Each step, name a worry, leave it.","Sit at the end. Calm water.","Horizon stretches infinitely.","Breathe with gentle waves.","Scan body, release tension.","Walk back. Thoughts have faded.","Carry only the stillness."]}]},
  {id:"stressed",name:"Stressed",emoji:"😤",color:"rgba(210,160,140,0.9)",bg:"rgba(210,160,140,0.08)",border:"rgba(210,160,140,0.2)",message:"Let's set some of it down.",sessions:[
    {id:"str-q",length:"quick",label:"Pressure Valve",mins:2,steps:["Clench fists tight. Hold 5s. Release.","Shoulders to ears. Hold. Drop.","Enormous inhale... sigh it all out.","Tension versus relief.","Repeat. More released than you realise."]},
    {id:"str-m",length:"medium",label:"Load Lightening",mins:5,steps:["Heavy backpack on shoulders.","Each stone weighs on you.","First stone out. Name it. Set down.","Next stone. Name it. Set down.","Continue until lighter.","Stand taller. Roll shoulders.","Three deep breaths in new space.","Pick up later. Enjoy lighter."]},
    {id:"str-d",length:"deep",label:"Mountain Stillness",mins:10,steps:["Five settling breaths.","Relax: face, jaw, neck, shoulders.","Through chest, belly, hips, legs, feet.","You are a great mountain.","Stress is weather: clouds, rain, wind.","Mountain doesn't fight weather.","Feel storm around you. Don't resist.","Simply observe. Unchanged.","Storm quiets. Sun breaks through.","Warmth returns.","Feel it seep into your body.","Carry the mountain's strength."]}]},
  {id:"sad",name:"Sad",emoji:"😢",color:"rgba(150,165,200,0.9)",bg:"rgba(150,165,200,0.08)",border:"rgba(150,165,200,0.2)",message:"Be gentle with yourself.",sessions:[
    {id:"sad-q",length:"quick",label:"Gentle Embrace",mins:2,steps:["Hands over heart.","Breathe in: 'I'm here for you.'","Exhale: 'It's okay to feel this.'","'This too shall pass.'","You deserve this kindness."]},
    {id:"sad-m",length:"medium",label:"Rain & Clearing",mins:5,steps:["Under gentle rain.","Drops carry pieces of sadness.","Don't stop the rain. Witness.","Drops are cleansing, not hurting.","Rain begins to lighten.","Blue sky appears. Warmth.","Gentler now. That's okay.","Freshness after the rain."]},
    {id:"sad-d",length:"deep",label:"Compassion Lake",mins:10,steps:["Permission to feel.","Breathe softly. Just breathe.","Acknowledge what's making you sad.","Place sadness on a still lake.","Ripples spread and fade.","The lake absorbs it completely.","Someone who loves you sits beside you.","They don't speak. Simply there.","Comfort of being witnessed.","Lake returns to stillness.","Send yourself love.","The lake is always there."]}]},
  {id:"angry",name:"Angry",emoji:"😠",color:"rgba(210,150,150,0.9)",bg:"rgba(210,150,150,0.08)",border:"rgba(210,150,150,0.2)",message:"Let's listen without letting it take the wheel.",sessions:[
    {id:"ang-q",length:"quick",label:"Steam Release",mins:2,steps:["Inhale 4 counts.","Exhale forcefully, fogging a mirror.","Five times, releasing heat.","Shake hands 10 seconds.","Stop. Fire turned down."]},
    {id:"ang-m",length:"medium",label:"Flame to Ember",mins:5,steps:["Anger as a flame in your chest.","Study it. Colour? Height?","'I see why you're here.'","Breathe slowly. Flame flickers lower.","Becomes ember. Warm, not burning.","Embers hold power without destruction.","What do I need right now?","Thank anger. Let ember rest."]},
    {id:"ang-d",length:"deep",label:"River of Release",mins:10,steps:["Breaths to settle racing energy.","Where does anger live? Jaw? Fists?","Breathe into that place.","A powerful river in a canyon.","Anger is the water — crashing.","You are the canyon. Containing.","Water rages. Allowed to be loud.","Terrain levels. River widens.","Same water flows gently now.","Anger transformed to clarity.","What boundary was crossed?","Honoured anger can guide."]}]},
  {id:"overwhelmed",name:"Overwhelmed",emoji:"🌀",color:"rgba(180,165,200,0.9)",bg:"rgba(180,165,200,0.08)",border:"rgba(180,165,200,0.2)",message:"The bravest thing is to pause.",sessions:[
    {id:"ovr-q",length:"quick",label:"One Thing",mins:1,steps:["Stop. Not everything, not now.","One breath. Best of your day.","One object. Focus only on it.","Study 30 seconds. Nothing else.","You can focus. That's enough."]},
    {id:"ovr-m",length:"medium",label:"Sorting Room",mins:5,steps:["Three boxes: Now, Later, Never.","Worries float like papers.","Catch one. Now, Later, or Never?","Place it. Satisfaction.","Next. Trust your instinct.","Air clears.","'Now' box is small. Manageable.","Just what's in 'Now.'"]},
    {id:"ovr-d",length:"deep",label:"Infinite Space",mins:10,steps:["Lie down. Be supported.","Breathe naturally. No goals.","Float in warm gentle darkness.","No walls, edges, deadlines.","Every demand dissolves.","Weightless. Nowhere to be.","Float. Breathe. Nothingness holds you.","What matters most rises gently.","Not a task — a feeling.","Hold this one thing close.","Feel body: fingertips, toes.","You've touched your centre."]}]},
  {id:"restless",name:"Restless",emoji:"⚡",color:"rgba(200,190,130,0.9)",bg:"rgba(200,190,130,0.08)",border:"rgba(200,190,130,0.2)",message:"Channel it instead of fighting it.",sessions:[
    {id:"rst-q",length:"quick",label:"Energy Reset",mins:2,steps:["Shake whole body 15 seconds.","Stop suddenly. Feel buzzing.","Three deep breaths downward.","Neck rolls, twice each way.","Edge softened. Reset."]},
    {id:"rst-m",length:"medium",label:"Channel & Focus",mins:5,steps:["Feel restless energy. Where?","Spinning wheel of light.","Direct it down your arms.","Open/close fists rhythmically.","Direct through legs into earth.","Draining from chest, shoulders.","Wheel slows. Balanced.","Energy is fuel, not friction."]},
    {id:"rst-d",length:"deep",label:"Still Point",mins:10,steps:["Hardest part — that's okay.","Count breaths to 10.","Restart with kindness.","Centre of a spinning top.","World whirls. Centre is still.","Finding that centre point.","Breathe into stillness.","Still point expands.","Notice fidget urges. Let pass.","Simply witness impulses.","Minute in expanded stillness.","Still point lives inside you."]}]},
  {id:"lonely",name:"Lonely",emoji:"🌙",color:"rgba(160,175,200,0.9)",bg:"rgba(160,175,200,0.08)",border:"rgba(160,175,200,0.2)",message:"Your heart is open and seeking.",sessions:[
    {id:"lon-q",length:"quick",label:"Self-Connection",mins:2,steps:["Hand over heart.","Someone happy to hear from you.","'May you be well.'","'May I be well.'","Connected through caring."]},
    {id:"lon-m",length:"medium",label:"Web of Connection",mins:5,steps:["Golden thread from your heart.","Reaches someone you love.","Another to a friend. Family.","Colleague, neighbour, kind stranger.","Centre of a beautiful web.","Threads remain always.","Tug one. Feel it vibrate.","Woven into others' lives."]},
    {id:"lon-d",length:"deep",label:"Campfire Circle",mins:10,steps:["Welcome some guests.","Warm campfire in a clearing.","Feel its warmth.","People you love arrive.","Presence fills the space.","Warmth of being known.","Past people join too.","Even those far away.","All connections live in you.","Someone smiles across flames.","Sit with belonging.","Fire always burning. Always here."]}]},
  {id:"unfocused",name:"Unfocused",emoji:"🌫️",color:"rgba(170,185,170,0.9)",bg:"rgba(170,185,170,0.08)",border:"rgba(170,185,170,0.2)",message:"Just needs a gentle breeze to clear.",sessions:[
    {id:"foc-q",length:"quick",label:"Sharp Breath",mins:1,steps:["Sit straight. Shoulders back.","5 quick sharp nose breaths.","Long slow mouth exhale.","Three times. Fog lifts.","Eyes wide. Sharper now."]},
    {id:"foc-m",length:"medium",label:"Laser Focus",mins:5,steps:["Mind: room of open tabs.","Close the worry tab.","Close dinner tab. Social media.","Until one remains: now.","Focus on breathing. One tab.","New tab? Gently close.","One minute, pure focus.","Carry clarity forward."]},
    {id:"foc-d",length:"deep",label:"Fog Clearing",mins:10,steps:["Five deep breaths.","Mind: a valley in thick fog.","Can barely see. That's okay.","Walk slowly. Trust each step.","A breeze stirs as you breathe.","Fog thins. Trees, path emerge.","Breeze grows warmer.","Path clear now.","In the clearing: what matters most.","Walk toward it. Waiting for you.","Full visibility. Full presence.","Fog was never permanent."]}]},
];
const LEN_META={quick:{label:"Quick Fix",icon:"⚡",desc:"1–2 min"},medium:{label:"Steady Calm",icon:"🌤️",desc:"5 min"},deep:{label:"Deep Dive",icon:"🌊",desc:"10 min"}};
const TRACKER_MOODS=[{id:"great",emoji:"😊",label:"Great",score:5,color:"rgba(150,200,150,0.9)"},{id:"good",emoji:"🙂",label:"Good",score:4,color:"rgba(170,195,140,0.9)"},{id:"okay",emoji:"😐",label:"Okay",score:3,color:"rgba(200,190,130,0.9)"},{id:"low",emoji:"😔",label:"Low",score:2,color:"rgba(190,165,140,0.9)"},{id:"rough",emoji:"😢",label:"Rough",score:1,color:"rgba(170,150,180,0.9)"}];
const TIME_SLOTS=[{id:"morning",label:"Morning",icon:"🌅",hours:"6am – 12pm"},{id:"afternoon",label:"Afternoon",icon:"☀️",hours:"12pm – 6pm"},{id:"evening",label:"Evening",icon:"🌙",hours:"6pm – 12am"}];
const JOURNAL_PROMPTS=["What came up during that session?","What are you carrying right now?","What would you like to let go of today?","What made you smile recently?","What does peace feel like in your body?","Write a kind sentence to yourself.","What's one small win from today?","What are you grateful for right now?"];
const SOUNDSCAPES=[{id:"rain",name:"Gentle Rain",icon:"🌧️",color:"rgba(140,170,200,0.9)"},{id:"forest",name:"Forest Morning",icon:"🌲",color:"rgba(130,180,130,0.9)"},{id:"ocean",name:"Ocean Waves",icon:"🌊",color:"rgba(130,175,210,0.9)"},{id:"fire",name:"Crackling Fire",icon:"🔥",color:"rgba(210,160,120,0.9)"},{id:"wind",name:"Mountain Wind",icon:"🏔️",color:"rgba(180,190,200,0.9)"},{id:"birds",name:"Birdsong",icon:"🐦",color:"rgba(200,190,140,0.9)"},{id:"bowls",name:"Singing Bowls",icon:"🔔",color:"rgba(190,170,200,0.9)"},{id:"stream",name:"Babbling Stream",icon:"💧",color:"rgba(150,195,200,0.9)"}];
const ESCAPE_JOURNEYS=[
  {id:"ej1",name:"Autumn Forest Walk",mins:10,icon:"🍂",color:"rgba(200,160,100,0.9)",steps:["You stand at the edge of an ancient forest in autumn.","Golden and amber leaves carpet the ground beneath your feet.","Step onto the soft path. Hear leaves crunch gently with each step.","Sunlight filters through the canopy in warm, honey-coloured beams.","A gentle breeze carries the scent of earth and wood smoke.","You pass a mossy boulder. Trail your fingers across its cool surface.","The path opens to a clearing with a still pond reflecting the trees.","Sit beside the water. Watch a single leaf spiral down to the surface.","Ripples spread outward, then stillness returns.","Breathe in the peace of this timeless place.","When ready, rise and walk back, carrying the forest's calm.","The path behind you glows golden. You are renewed."]},
  {id:"ej2",name:"Moonlit Beach",mins:8,icon:"🌕",color:"rgba(160,175,210,0.9)",steps:["You arrive at a deserted beach under a full moon.","Silver light paints everything in soft blue and white.","Sand is cool and smooth beneath your bare feet.","Waves arrive in slow, rhythmic pulses.","Walk along the waterline. Foam tickles your ankles.","Find a smooth piece of driftwood and sit.","The moon's reflection stretches across the water toward you.","Each wave brings calm. Each retreat takes worry.","Stars multiply above — countless points of light.","You are small and held by something vast and gentle.","Let the sound of the ocean fill every corner of your mind.","Rise when ready. The beach will wait for you."]},
  {id:"ej3",name:"Mountain Sunrise",mins:10,icon:"🏔️",color:"rgba(210,170,140,0.9)",steps:["You sit on a mountain summit before dawn.","The air is crisp and clean. Stars still visible overhead.","A thin line of gold appears on the eastern horizon.","It spreads — peach, then rose, then blazing amber.","The first ray of sun touches your face. Feel its warmth.","Valleys below fill with golden mist.","Mountains emerge like islands in a sea of light.","Birds begin their morning songs far below.","The whole world wakes up around you.","You were here to witness the very first moment.","Gratitude fills your chest like the spreading light.","Carry this dawn within you throughout your day."]},
];

// ============================================================
// INSIGHT GENERATOR
// ============================================================
function genInsights(logs){
  const entries=Object.entries(logs).sort((a,b)=>a[0].localeCompare(b[0]));
  if(!entries.length)return{weekAvg:null,monthAvg:null,trend:null,bestTime:null,worstTime:null,streak:0,totalLogs:0,recommendations:[],timeAvgs:{}};
  const now=new Date(),wa=new Date(now),ma=new Date(now);wa.setDate(wa.getDate()-7);ma.setDate(ma.getDate()-30);
  let ws=[],ms=[],all=[];const ts={morning:[],afternoon:[],evening:[]};
  entries.forEach(([k,sl])=>{const d=new Date(k+"T12:00:00");Object.entries(sl).forEach(([s,mid])=>{const m=TRACKER_MOODS.find(x=>x.id===mid);if(!m)return;all.push(m.score);if(d>=wa)ws.push(m.score);if(d>=ma)ms.push(m.score);ts[s]?.push(m.score)})});
  const avg=a=>a.length?a.reduce((x,y)=>x+y,0)/a.length:null;
  const weekAvg=avg(ws),monthAvg=avg(ms);
  const twa=new Date(now);twa.setDate(twa.getDate()-14);let pw=[];
  entries.forEach(([k,sl])=>{const d=new Date(k+"T12:00:00");if(d>=twa&&d<wa)Object.values(sl).forEach(mid=>{const m=TRACKER_MOODS.find(x=>x.id===mid);if(m)pw.push(m.score)})});
  const pa=avg(pw);let trend=null;if(weekAvg!==null&&pa!==null){const diff=weekAvg-pa;trend=diff>0.3?"improving":diff<-0.3?"declining":"stable"}
  const ta={};Object.entries(ts).forEach(([k,v])=>{ta[k]=avg(v)});
  const vt=Object.entries(ta).filter(([,v])=>v!==null);
  const bestTime=vt.length?vt.reduce((a,b)=>a[1]>b[1]?a:b)[0]:null;
  const worstTime=vt.length?vt.reduce((a,b)=>a[1]<b[1]?a:b)[0]:null;
  let streak=0;for(let i=0;i<365;i++){const d=new Date(now);d.setDate(d.getDate()-i);if(logs[dk(d)]&&Object.keys(logs[dk(d)]).length>0)streak++;else break}
  const recs=[];
  if(weekAvg!==null){if(weekAvg<2.5){recs.push({icon:"💚",text:"Tough week. Try a Deep Dive session.",act:"sessions"});recs.push({icon:"🌿",text:"Connecting with someone can help.",act:null})}
  else if(weekAvg<3.5)recs.push({icon:"🌤️",text:"Mixed week. Steady Calm sessions help.",act:"sessions"});
  else recs.push({icon:"✨",text:"Doing well! Keep momentum with daily practice.",act:"minutes"})}
  if(worstTime==="morning")recs.push({icon:"🌅",text:"Mornings are hardest. Try breathing before phone.",act:null});
  else if(worstTime==="evening")recs.push({icon:"🌙",text:"Evenings dip. Body scan before bed helps.",act:null});
  if(trend==="declining")recs.push({icon:"🫂",text:"Trending down. Be extra gentle.",act:null});
  else if(trend==="improving")recs.push({icon:"🌱",text:"Trending up — keep doing what works!",act:null});
  if(streak>=7)recs.push({icon:"🔥",text:`${streak}-day streak! Consistency builds awareness.`,act:null});
  return{weekAvg,monthAvg,trend,bestTime,worstTime,streak,totalLogs:all.length,recommendations:recs,timeAvgs:ta};}

function scoreToMood(s){if(s===null)return{emoji:"—",label:"No data",color:"rgba(255,255,255,0.2)"};if(s>=4.5)return{emoji:"😊",label:"Great",color:"rgba(150,200,150,0.9)"};if(s>=3.5)return{emoji:"🙂",label:"Good",color:"rgba(170,195,140,0.9)"};if(s>=2.5)return{emoji:"😐",label:"Okay",color:"rgba(200,190,130,0.9)"};if(s>=1.5)return{emoji:"😔",label:"Low",color:"rgba(190,165,140,0.9)"};return{emoji:"😢",label:"Rough",color:"rgba(170,150,180,0.9)"};}


// ============================================================
// EXERCISE TIMER (preview → ready → active → complete → journal)
// ============================================================
function ExerciseTimer({exercise,onClose,onFav,isFav,onComplete,onJournal}){
  const isB=exercise.cat==="breathing";const bp=isB?getBreathingPattern(exercise):null;
  const [cMins,setCMins]=useState(exercise.mins);
  const [phase,setPhase]=useState("preview");
  const [timeLeft,setTimeLeft]=useState(cMins*60);
  const [step,setStep]=useState(0);
  const [elapsed,setElapsed]=useState(0);
  const ref=useRef(null);
  const cat=CATEGORIES.find(c=>c.id===exercise.cat);
  useEffect(()=>{if(phase==="preview"||phase==="ready")setTimeLeft(cMins*60)},[cMins,phase]);
  useEffect(()=>{if(phase==="active"){ref.current=setInterval(()=>{setElapsed(e=>e+1);setTimeLeft(t=>{if(t<=1){clearInterval(ref.current);setPhase("complete");onComplete?.();return 0}return t-1})},1000)}return()=>clearInterval(ref.current)},[phase]);
  useEffect(()=>{if(phase==="active"&&!isB){const sd=cMins*60/exercise.steps.length;setStep(Math.min(Math.floor((cMins*60-timeLeft)/sd),exercise.steps.length-1))}},[timeLeft,phase,cMins,exercise.steps.length,isB]);
  const getBI=()=>{if(!bp||phase!=="active")return"";const{phase:b}=getBreathPhase(bp,elapsed);return bp.ins[b]||""};
  const progress=phase==="preview"||phase==="ready"?0:phase==="complete"?1:(cMins*60-timeLeft)/(cMins*60);
  const m=Math.floor(timeLeft/60),s=timeLeft%60;
  const ci=isB?getBI():exercise.steps[step];

  if(phase==="preview")return(
    <div style={{position:"fixed",inset:0,zIndex:150,background:"rgba(8,12,10,0.97)",display:"flex",flexDirection:"column",alignItems:"center",padding:"2rem",overflowY:"auto",animation:"fadeUp 0.5s ease"}}>
      <button onClick={onClose} style={{position:"absolute",top:"1.5rem",right:"1.5rem",background:"none",border:"none",color:"rgba(255,255,255,0.3)",cursor:"pointer",fontSize:"1.5rem",padding:"0.5rem"}}>✕</button>
      {onFav&&<button onClick={onFav} style={{position:"absolute",top:"1.5rem",left:"1.5rem",background:"none",border:"none",color:isFav?"rgba(210,170,180,0.9)":"rgba(255,255,255,0.2)",cursor:"pointer",fontSize:"1.3rem",padding:"0.5rem"}}>{isFav?"♥":"♡"}</button>}
      <div style={{maxWidth:480,width:"100%",marginTop:"1rem"}}>
        <span style={{fontFamily:FK,fontSize:"0.65rem",color:cat.color,letterSpacing:"0.2em",textTransform:"uppercase",opacity:0.7,display:"block",textAlign:"center",marginBottom:"0.5rem"}}>{cat.icon} {cat.name}</span>
        <h2 style={{fontFamily:FS,fontSize:"1.8rem",fontWeight:300,color:"rgba(255,255,255,0.88)",textAlign:"center",marginBottom:"0.5rem"}}>{exercise.name}</h2>
        {isB?(<div style={{marginBottom:"2rem"}}>
          <span style={{fontFamily:FK,fontSize:"0.6rem",color:"rgba(255,255,255,0.25)",letterSpacing:"0.15em",textTransform:"uppercase",display:"block",textAlign:"center",marginBottom:"0.75rem"}}>Duration</span>
          <div style={{display:"flex",justifyContent:"center",gap:"0.5rem"}}>
            {[1,2,3,5].map(d=>{const a=cMins===d;return(<button key={d} onClick={()=>setCMins(d)} style={{width:56,height:56,borderRadius:"50%",background:a?cat.bg:"rgba(255,255,255,0.025)",border:`1.5px solid ${a?cat.border:"rgba(255,255,255,0.08)"}`,display:"flex",flexDirection:"column",alignItems:"center",justifyContent:"center",cursor:"pointer",transition:"all 0.3s ease",transform:a?"scale(1.1)":"scale(1)"}}>
              <span style={{fontFamily:FK,fontSize:"1rem",color:a?cat.color:"rgba(255,255,255,0.4)"}}>{d}</span>
              <span style={{fontFamily:FK,fontSize:"0.45rem",color:a?cat.color:"rgba(255,255,255,0.2)"}}>min</span></button>)})}
          </div>
          <div style={{background:"rgba(255,255,255,0.025)",border:"1px solid rgba(255,255,255,0.06)",borderRadius:14,padding:"1rem",marginTop:"1.25rem",textAlign:"center"}}>
            <span style={{fontFamily:FK,fontSize:"0.6rem",color:cat.color,letterSpacing:"0.12em",textTransform:"uppercase",display:"block",marginBottom:"0.5rem",opacity:0.7}}>{bp.label} Pattern</span>
            <div style={{display:"flex",justifyContent:"center",gap:"0.75rem",flexWrap:"wrap"}}>
              <span style={{fontFamily:FK,fontSize:"0.75rem",color:"rgba(150,200,170,0.8)"}}>↑ In {bp.inhale}s</span>
              {bp.hold1>0&&<span style={{fontFamily:FK,fontSize:"0.75rem",color:"rgba(200,190,150,0.7)"}}>· Hold {bp.hold1}s</span>}
              <span style={{fontFamily:FK,fontSize:"0.75rem",color:"rgba(150,170,200,0.8)"}}>↓ Out {bp.exhale}s</span>
              {bp.hold2>0&&<span style={{fontFamily:FK,fontSize:"0.75rem",color:"rgba(200,190,150,0.7)"}}>· Hold {bp.hold2}s</span>}
            </div>
          </div>
        </div>):(<div style={{display:"flex",justifyContent:"center",gap:"1rem",marginBottom:"2rem"}}>
          <span style={{fontFamily:FK,fontSize:"0.7rem",color:"rgba(255,255,255,0.3)"}}>{exercise.mins} min · {exercise.steps.length} steps</span></div>)}
        <div style={{marginBottom:"2.5rem"}}>
          <span style={{fontFamily:FK,fontSize:"0.6rem",color:"rgba(255,255,255,0.25)",letterSpacing:"0.15em",textTransform:"uppercase",display:"block",marginBottom:"1rem"}}>{isB?"How it works":"What you'll do"}</span>
          {exercise.steps.map((st,i)=>(<div key={i} style={{display:"flex",gap:"1rem",alignItems:"flex-start",marginBottom:"0.75rem",animation:`fadeUp 0.4s ease ${i*0.06}s both`}}>
            <div style={{width:28,height:28,borderRadius:"50%",flexShrink:0,background:cat.bg,border:`1px solid ${cat.border}`,display:"flex",alignItems:"center",justifyContent:"center",fontFamily:FK,fontSize:"0.65rem",color:cat.color}}>{i+1}</div>
            <p style={{fontFamily:FK,fontSize:"0.85rem",color:"rgba(255,255,255,0.6)",lineHeight:1.6,margin:0,paddingTop:"0.2rem"}}>{st}</p></div>))}
        </div>
        <div style={{textAlign:"center"}}><button onClick={()=>setPhase("ready")} style={{background:cat.bg,border:`1px solid ${cat.border}`,borderRadius:100,padding:"0.9rem 3rem",fontFamily:FK,fontSize:"0.85rem",color:cat.color,letterSpacing:"0.15em",textTransform:"uppercase",cursor:"pointer"}}>I'm Ready</button></div>
      </div></div>);

  if(phase==="ready")return(
    <div style={{position:"fixed",inset:0,zIndex:150,background:"rgba(8,12,10,0.97)",display:"flex",flexDirection:"column",alignItems:"center",justifyContent:"center",padding:"2rem",animation:"fadeUp 0.5s ease"}}>
      <button onClick={onClose} style={{position:"absolute",top:"1.5rem",right:"1.5rem",background:"none",border:"none",color:"rgba(255,255,255,0.3)",cursor:"pointer",fontSize:"1.5rem",padding:"0.5rem"}}>✕</button>
      <span style={{fontFamily:FK,fontSize:"0.65rem",color:cat.color,letterSpacing:"0.2em",textTransform:"uppercase",marginBottom:"0.5rem",opacity:0.7}}>{cat.icon} {cat.name}</span>
      <h2 style={{fontFamily:FS,fontSize:"1.6rem",fontWeight:300,color:"rgba(255,255,255,0.85)",marginBottom:"2rem"}}>{exercise.name}</h2>
      <div className="breathing-circle" style={{width:100,height:100,marginBottom:"2rem",border:`1.5px solid ${cat.color.replace("0.9","0.3")}`}}/>
      <p style={{fontFamily:FS,fontSize:"1.2rem",color:"rgba(255,255,255,0.55)",fontStyle:"italic",textAlign:"center",maxWidth:360,lineHeight:1.6,marginBottom:"0.75rem"}}>Find a comfortable position. When you're settled, begin.</p>
      <span style={{fontFamily:FK,fontSize:"0.7rem",color:"rgba(255,255,255,0.25)",marginBottom:"2.5rem"}}>{cMins} min</span>
      <div style={{display:"flex",gap:"1rem"}}>
        <button onClick={()=>setPhase("preview")} style={{background:"rgba(255,255,255,0.04)",border:"1px solid rgba(255,255,255,0.1)",borderRadius:100,padding:"0.8rem 1.8rem",fontFamily:FK,fontSize:"0.75rem",color:"rgba(255,255,255,0.35)",letterSpacing:"0.1em",textTransform:"uppercase",cursor:"pointer"}}>Review</button>
        <button onClick={()=>{setTimeLeft(cMins*60);setElapsed(0);setStep(0);setPhase("active")}} style={{background:cat.bg,border:`1px solid ${cat.border}`,borderRadius:100,padding:"0.8rem 2.5rem",fontFamily:FK,fontSize:"0.85rem",color:cat.color,letterSpacing:"0.15em",textTransform:"uppercase",cursor:"pointer"}}>Begin</button>
      </div></div>);

  return(
    <div style={{position:"fixed",inset:0,zIndex:150,background:"rgba(8,12,10,0.96)",display:"flex",flexDirection:"column",alignItems:"center",justifyContent:"center",padding:"2rem",animation:"fadeUp 0.5s ease"}}>
      <button onClick={()=>{clearInterval(ref.current);onClose()}} style={{position:"absolute",top:"1.5rem",right:"1.5rem",background:"none",border:"none",color:"rgba(255,255,255,0.3)",cursor:"pointer",fontSize:"1.5rem",padding:"0.5rem"}}>✕</button>
      <span style={{fontFamily:FK,fontSize:"0.65rem",color:cat.color,letterSpacing:"0.2em",textTransform:"uppercase",marginBottom:"0.5rem",opacity:0.7}}>{cat.icon} {cat.name}</span>
      <h2 style={{fontFamily:FS,fontSize:"1.4rem",fontWeight:300,color:"rgba(255,255,255,0.85)",marginBottom:"1.25rem"}}>{exercise.name}</h2>
      {isB&&phase==="active"&&<BreathingGuide pattern={bp} elapsed={elapsed}/>}
      <div style={{position:"relative",width:180,height:180,marginBottom:"1.5rem"}}>
        <svg width="180" height="180" style={{transform:"rotate(-90deg)"}}><circle cx="90" cy="90" r="82" fill="none" stroke="rgba(255,255,255,0.05)" strokeWidth="3"/><circle cx="90" cy="90" r="82" fill="none" stroke={cat.color} strokeWidth="3" strokeLinecap="round" strokeDasharray={2*Math.PI*82} strokeDashoffset={2*Math.PI*82*(1-progress)} style={{transition:"stroke-dashoffset 1s linear",opacity:0.7}}/></svg>
        <div style={{position:"absolute",inset:0,display:"flex",flexDirection:"column",alignItems:"center",justifyContent:"center"}}>
          {phase==="complete"?<span style={{fontFamily:FS,fontSize:"1.3rem",color:cat.color}}>namaste</span>:(<>
            <span style={{fontFamily:FK,fontSize:"2.5rem",fontWeight:300,color:"rgba(255,255,255,0.85)"}}>{m}:{String(s).padStart(2,"0")}</span>
            <span style={{fontFamily:FK,fontSize:"0.55rem",color:"rgba(255,255,255,0.3)",letterSpacing:"0.15em",textTransform:"uppercase"}}>remaining</span></>)}
        </div></div>
      <div style={{maxWidth:440,textAlign:"center",minHeight:70,display:"flex",alignItems:"center",justifyContent:"center",marginBottom:"1.25rem"}}>
        <p style={{fontFamily:FS,fontSize:"1.05rem",lineHeight:1.65,color:"rgba(255,255,255,0.6)",fontWeight:300,fontStyle:"italic"}}>{phase==="complete"?"You did beautifully. Carry this stillness with you.":ci}</p></div>
      {!isB&&phase==="active"&&<div style={{display:"flex",gap:8,marginBottom:"1.5rem"}}>{exercise.steps.map((_,i)=><div key={i} style={{width:6,height:6,borderRadius:"50%",background:i<=step?cat.color:"rgba(255,255,255,0.1)",transition:"background 0.5s ease"}}/>)}</div>}
      {isB&&phase==="active"&&<span style={{fontFamily:FK,fontSize:"0.6rem",color:"rgba(255,255,255,0.2)",letterSpacing:"0.12em",marginBottom:"1.5rem"}}>Cycle {Math.floor(elapsed/(bp.inhale+bp.hold1+bp.exhale+bp.hold2))+1}</span>}
      {phase==="active"&&<button onClick={()=>{clearInterval(ref.current);setPhase("ready");setTimeLeft(cMins*60);setStep(0);setElapsed(0)}} style={{background:"rgba(255,255,255,0.04)",border:"1px solid rgba(255,255,255,0.1)",borderRadius:100,padding:"0.7rem 2rem",fontFamily:FK,fontSize:"0.75rem",color:"rgba(255,255,255,0.35)",letterSpacing:"0.12em",textTransform:"uppercase",cursor:"pointer"}}>Reset</button>}
      {phase==="complete"&&<div style={{display:"flex",gap:"0.75rem"}}>
        {onJournal&&<button onClick={onJournal} style={{background:"rgba(255,255,255,0.04)",border:"1px solid rgba(255,255,255,0.1)",borderRadius:100,padding:"0.8rem 1.5rem",fontFamily:FK,fontSize:"0.75rem",color:"rgba(255,255,255,0.4)",letterSpacing:"0.1em",textTransform:"uppercase",cursor:"pointer"}}>📝 Journal</button>}
        <button onClick={onClose} style={{background:cat.bg,border:`1px solid ${cat.border}`,borderRadius:100,padding:"0.85rem 2.5rem",fontFamily:FK,fontSize:"0.8rem",color:cat.color,letterSpacing:"0.15em",textTransform:"uppercase",cursor:"pointer"}}>Return</button>
      </div>}
    </div>);
}

// ============================================================
// JOURNAL OVERLAY
// ============================================================
function JournalOverlay({onClose,onSave,exerciseName}){
  const [text,setText]=useState("");
  const prompt=JOURNAL_PROMPTS[Math.floor(Math.random()*JOURNAL_PROMPTS.length)];
  return(
    <div style={{position:"fixed",inset:0,zIndex:160,background:"rgba(8,12,10,0.97)",display:"flex",flexDirection:"column",alignItems:"center",justifyContent:"center",padding:"2rem",animation:"fadeUp 0.5s ease"}}>
      <button onClick={onClose} style={{position:"absolute",top:"1.5rem",right:"1.5rem",background:"none",border:"none",color:"rgba(255,255,255,0.3)",cursor:"pointer",fontSize:"1.5rem",padding:"0.5rem"}}>✕</button>
      <span style={{fontFamily:FK,fontSize:"0.6rem",color:"rgba(255,255,255,0.25)",letterSpacing:"0.15em",textTransform:"uppercase",marginBottom:"0.5rem"}}>Journal Entry</span>
      {exerciseName&&<span style={{fontFamily:FK,fontSize:"0.7rem",color:"rgba(167,199,168,0.6)",marginBottom:"1.5rem"}}>After: {exerciseName}</span>}
      <p style={{fontFamily:FS,fontSize:"1.15rem",color:"rgba(255,255,255,0.5)",fontStyle:"italic",textAlign:"center",maxWidth:360,lineHeight:1.6,marginBottom:"1.5rem"}}>{prompt}</p>
      <textarea value={text} onChange={e=>setText(e.target.value)} placeholder="Write freely..." style={{width:"100%",maxWidth:440,height:160,background:"rgba(255,255,255,0.03)",border:"1px solid rgba(255,255,255,0.08)",borderRadius:14,padding:"1rem",fontFamily:FK,fontSize:"0.85rem",color:"rgba(255,255,255,0.7)",resize:"none",lineHeight:1.6,outline:"none"}}/>
      <div style={{display:"flex",gap:"0.75rem",marginTop:"1.5rem"}}>
        <button onClick={onClose} style={{background:"rgba(255,255,255,0.04)",border:"1px solid rgba(255,255,255,0.1)",borderRadius:100,padding:"0.7rem 1.5rem",fontFamily:FK,fontSize:"0.75rem",color:"rgba(255,255,255,0.35)",cursor:"pointer"}}>Skip</button>
        <button onClick={()=>{if(text.trim())onSave(text.trim());onClose()}} style={{background:"rgba(167,199,168,0.08)",border:"1px solid rgba(167,199,168,0.15)",borderRadius:100,padding:"0.7rem 2rem",fontFamily:FK,fontSize:"0.75rem",color:"rgba(167,199,168,0.8)",cursor:"pointer",letterSpacing:"0.1em"}}>Save Entry</button>
      </div>
    </div>);
}

// ============================================================
// SVG SCENE GENERATOR
// ============================================================
function generateScene(seed){const rng=s=>{s=Math.sin(s*127.1+311.7)*43758.5453;return s-Math.floor(s)};let se=seed||Math.random()*10000;const r=()=>{se+=1;return rng(se)};
const scenes=[{sky:["#1a1a3e","#2d1b4e","#6b3a5e","#c76b4a","#f4a55a"],water:"#1a2040",mountains:["#1a1a2e","#252545","#35355a"],hasMoon:true,hasStars:true,hasReflection:true},{sky:["#0d1b2a","#1b3a4b","#3a6b6b","#7ab5a0","#c8dbbe"],water:"#0d1b2a",mountains:["#0a1612","#122a20","#1a3a2a"],hasMoon:false,hasStars:false,hasReflection:true},{sky:["#0a0e27","#151d4a","#2a3a7a","#5a6aaa","#8a9acc"],water:"#0a0e27",mountains:["#080c1a","#101830","#1a2848"],hasMoon:true,hasStars:true,hasReflection:true},{sky:["#050a15","#0a1a2a","#0d2a3a","#1a4a4a","#0a2a3a"],water:"#050a15",mountains:["#040810","#081018","#0c1820"],hasMoon:false,hasStars:true,hasReflection:true,hasAurora:true}];
const scene=scenes[Math.floor(r()*scenes.length)];const stars=[];if(scene.hasStars)for(let i=0;i<60;i++)stars.push({x:r()*100,y:r()*40,size:r()*1.5+0.3,opacity:r()*0.6+0.2});
const ml=scene.mountains.map((c,i)=>{const pts=[];const by=45+i*10,amp=20-i*4;for(let x=0;x<=100;x+=5)pts.push(`${x},${by-r()*amp}`);pts.push("100,100","0,100");return{points:pts.join(" "),color:c}});
const trees=[];for(let i=0;i<15;i++)trees.push({x:r()*100,baseY:65+r()*10,height:3+r()*5,width:1+r()*2,opacity:0.3+r()*0.4});
return{...scene,id:Math.floor(se),stars,mountainLayers:ml,trees,moonX:15+r()*30,moonY:10+r()*15};}

function NatureScene({scene}){if(!scene)return null;const g=`sky-${scene.id}`,rfl=`refl-${scene.id}`;
return(<svg viewBox="0 0 100 100" preserveAspectRatio="xMidYMid slice" style={{position:"absolute",inset:0,width:"100%",height:"100%"}}>
<defs><linearGradient id={g} x1="0" y1="0" x2="0" y2="1">{scene.sky.map((c,i)=><stop key={i} offset={`${(i/(scene.sky.length-1))*100}%`} stopColor={c}/>)}</linearGradient></defs>
<rect width="100" height="100" fill={`url(#${g})`}/>
{scene.stars.map((st,i)=><circle key={i} cx={st.x} cy={st.y} r={st.size} fill="white" opacity={st.opacity}/>)}
{scene.hasMoon&&<g><circle cx={scene.moonX} cy={scene.moonY} r="4" fill="rgba(255,255,240,0.9)"/><circle cx={scene.moonX+1.2} cy={scene.moonY-0.8} r="3.5" fill={scene.sky[1]}/></g>}
{scene.mountainLayers.map((m,i)=><polygon key={i} points={m.points} fill={m.color}/>)}
{scene.trees.map((t,i)=><g key={i} opacity={t.opacity}><line x1={t.x} y1={t.baseY} x2={t.x} y2={t.baseY-t.height} stroke="#0a1a10" strokeWidth="0.3"/><ellipse cx={t.x} cy={t.baseY-t.height*0.7} rx={t.width} ry={t.height*0.5} fill="#0a1a10"/></g>)}
{scene.hasReflection&&<rect x="0" y="75" width="100" height="25" fill={scene.water} opacity="0.7"/>}
</svg>);}

// ============================================================
// SETTINGS PANEL
// ============================================================
function SettingsPanel({apiKey,setApiKey,isOpen,setIsOpen,notifications,setNotifications}){
  const [tempKey,setTempKey]=useState(apiKey);
  if(!isOpen)return null;
  return(
    <div style={{position:"fixed",inset:0,zIndex:200,display:"flex",alignItems:"center",justifyContent:"center",background:"rgba(0,0,0,0.6)",backdropFilter:"blur(8px)"}} onClick={e=>{if(e.target===e.currentTarget)setIsOpen(false)}}>
      <div style={{background:"rgba(18,24,20,0.95)",border:"1px solid rgba(255,255,255,0.1)",borderRadius:20,padding:"2rem",maxWidth:440,width:"90%",maxHeight:"80vh",overflowY:"auto"}}>
        <h3 style={{fontFamily:FS,fontSize:"1.4rem",fontWeight:300,color:"rgba(255,255,255,0.85)",marginBottom:"1.5rem"}}>Settings</h3>

        <label style={{fontFamily:FK,fontSize:"0.6rem",color:"rgba(255,255,255,0.4)",letterSpacing:"0.15em",textTransform:"uppercase",display:"block",marginBottom:"0.5rem"}}>Unsplash Key</label>
        <input value={tempKey} onChange={e=>setTempKey(e.target.value)} placeholder="Access Key" style={{width:"100%",padding:"0.7rem 1rem",background:"rgba(255,255,255,0.04)",border:"1px solid rgba(255,255,255,0.1)",borderRadius:10,color:"rgba(255,255,255,0.8)",fontFamily:FK,fontSize:"0.8rem",outline:"none",marginBottom:"0.75rem"}}/>
        <button onClick={()=>{setApiKey(tempKey);setIsOpen(false)}} style={{width:"100%",padding:"0.7rem",background:"rgba(167,199,168,0.1)",border:"1px solid rgba(167,199,168,0.2)",borderRadius:10,color:"rgba(167,199,168,0.8)",fontFamily:FK,fontSize:"0.75rem",cursor:"pointer",letterSpacing:"0.1em",marginBottom:"2rem"}}>Save Key</button>

        <label style={{fontFamily:FK,fontSize:"0.6rem",color:"rgba(255,255,255,0.4)",letterSpacing:"0.15em",textTransform:"uppercase",display:"block",marginBottom:"0.75rem"}}>Reminders</label>
        {["Morning check-in · 8am","Afternoon pause · 1pm","Evening wind-down · 9pm"].map((n,i)=>{
          const key=["morning","afternoon","evening"][i];const on=notifications[key];
          return(<div key={key} style={{display:"flex",alignItems:"center",justifyContent:"space-between",marginBottom:"0.6rem",padding:"0.6rem 0.75rem",background:"rgba(255,255,255,0.025)",borderRadius:10}}>
            <span style={{fontFamily:FK,fontSize:"0.75rem",color:"rgba(255,255,255,0.5)"}}>{n}</span>
            <button onClick={()=>setNotifications(p=>({...p,[key]:!on}))} style={{width:40,height:22,borderRadius:11,border:"none",background:on?"rgba(167,199,168,0.4)":"rgba(255,255,255,0.1)",cursor:"pointer",position:"relative",transition:"background 0.3s ease"}}>
              <div style={{width:16,height:16,borderRadius:"50%",background:on?"rgba(167,199,168,0.9)":"rgba(255,255,255,0.3)",position:"absolute",top:3,left:on?21:3,transition:"left 0.3s ease"}}/>
            </button></div>)})}
      </div></div>);}


// ============================================================
// ZEN PAGE (with daily intention)
// ============================================================
function ZenPage({unsplashKey,intention,setIntention}){
  const [img,setImg]=useState(null);const [scene]=useState(()=>generateScene());
  const [quote,setQuote]=useState({text:"In the middle of difficulty lies opportunity.",author:"Albert Einstein"});
  const [editIntent,setEditIntent]=useState(false);const [tempIntent,setTempIntent]=useState(intention||"");
  useEffect(()=>{if(unsplashKey){fetch(`https://api.unsplash.com/photos/random?query=nature+calm+minimal&orientation=landscape&client_id=${unsplashKey}`).then(r=>r.json()).then(d=>{if(d.urls)setImg(d.urls.regular)}).catch(()=>{})}
    fetch("https://zenquotes.io/api/random").then(r=>r.json()).then(d=>{if(d[0])setQuote({text:d[0].q,author:d[0].a})}).catch(()=>{fetch("https://api.adviceslip.com/advice").then(r=>r.json()).then(d=>{if(d.slip)setQuote({text:d.slip.advice,author:"Advice Slip"})}).catch(()=>{})})},[unsplashKey]);
  return(
    <div style={{minHeight:"100vh",position:"relative",overflow:"hidden"}}>
      {img?<img src={img} alt="" style={{position:"absolute",inset:0,width:"100%",height:"100%",objectFit:"cover",filter:"brightness(0.35) saturate(0.7)"}}/>:<NatureScene scene={scene}/>}
      <div style={{position:"relative",zIndex:1,display:"flex",flexDirection:"column",alignItems:"center",justifyContent:"center",minHeight:"100vh",padding:"2rem",textAlign:"center"}}>
        <div className="breathing-circle" style={{marginBottom:"2rem"}}/>
        <blockquote style={{fontFamily:FS,fontSize:"clamp(1.1rem, 4vw, 1.6rem)",fontWeight:300,fontStyle:"italic",color:"rgba(255,255,255,0.75)",maxWidth:520,lineHeight:1.7,marginBottom:"0.75rem"}}>{quote.text}</blockquote>
        <span style={{fontFamily:FK,fontSize:"0.65rem",color:"rgba(255,255,255,0.3)",letterSpacing:"0.15em"}}>— {quote.author}</span>
        <div style={{marginTop:"3rem",maxWidth:360,width:"100%"}}>
          {!editIntent?(<button onClick={()=>{setTempIntent(intention||"");setEditIntent(true)}} style={{background:"rgba(255,255,255,0.04)",backdropFilter:"blur(12px)",border:"1px solid rgba(255,255,255,0.08)",borderRadius:14,padding:"0.85rem 1.5rem",width:"100%",cursor:"pointer",textAlign:"center"}}>
            {intention?(<><span style={{fontFamily:FK,fontSize:"0.55rem",color:"rgba(167,199,168,0.5)",letterSpacing:"0.15em",textTransform:"uppercase",display:"block",marginBottom:"0.3rem"}}>Today's Intention</span>
              <span style={{fontFamily:FS,fontSize:"1.05rem",color:"rgba(255,255,255,0.7)",fontStyle:"italic"}}>{intention}</span></>):
            (<span style={{fontFamily:FK,fontSize:"0.7rem",color:"rgba(255,255,255,0.3)"}}>✦ Set a daily intention</span>)}
          </button>):(<div style={{background:"rgba(255,255,255,0.04)",backdropFilter:"blur(12px)",border:"1px solid rgba(255,255,255,0.08)",borderRadius:14,padding:"1rem"}}>
            <span style={{fontFamily:FK,fontSize:"0.55rem",color:"rgba(167,199,168,0.5)",letterSpacing:"0.15em",textTransform:"uppercase",display:"block",marginBottom:"0.6rem"}}>What's your intention today?</span>
            <input value={tempIntent} onChange={e=>setTempIntent(e.target.value)} placeholder="Be patient with myself…" maxLength={80} style={{width:"100%",background:"rgba(255,255,255,0.03)",border:"1px solid rgba(255,255,255,0.1)",borderRadius:10,padding:"0.6rem 0.8rem",fontFamily:FS,fontSize:"1rem",color:"rgba(255,255,255,0.8)",outline:"none",fontStyle:"italic"}}/>
            <div style={{display:"flex",gap:"0.5rem",marginTop:"0.75rem",justifyContent:"flex-end"}}>
              <button onClick={()=>setEditIntent(false)} style={{background:"none",border:"none",fontFamily:FK,fontSize:"0.7rem",color:"rgba(255,255,255,0.3)",cursor:"pointer"}}>Cancel</button>
              <button onClick={()=>{setIntention(tempIntent.trim());setEditIntent(false)}} style={{background:"rgba(167,199,168,0.1)",border:"1px solid rgba(167,199,168,0.15)",borderRadius:100,padding:"0.4rem 1.2rem",fontFamily:FK,fontSize:"0.7rem",color:"rgba(167,199,168,0.8)",cursor:"pointer"}}>Set</button></div></div>)}
        </div></div></div>);}

// ============================================================
// MINUTES PAGE (with favourites)
// ============================================================
function MinutesPage({favourites,toggleFav,stats,addStats,journal,addJournal}){
  const [selCat,setSelCat]=useState(null);const [activeEx,setActiveEx]=useState(null);const [showJournal,setShowJournal]=useState(false);const [showFavs,setShowFavs]=useState(false);
  const filtered=selCat?EXERCISES.filter(e=>e.cat===selCat):EXERCISES;
  const display=showFavs?EXERCISES.filter(e=>favourites.includes(e.id)):selCat?filtered:filtered.slice(0,12);
  return(
    <div style={{minHeight:"100vh",background:"linear-gradient(180deg, #0e100f 0%, #0a0f0c 100%)",padding:"2rem 1.25rem"}}>
      {activeEx&&<ExerciseTimer exercise={activeEx} onClose={()=>{setActiveEx(null);setShowJournal(false)}} isFav={favourites.includes(activeEx.id)} onFav={()=>toggleFav(activeEx.id)} onComplete={()=>addStats(activeEx)} onJournal={()=>setShowJournal(true)}/>}
      {showJournal&&<JournalOverlay exerciseName={activeEx?.name} onClose={()=>setShowJournal(false)} onSave={t=>addJournal(t,activeEx?.name)}/>}
      <div style={{textAlign:"center",marginBottom:"2rem",animation:"fadeUp 0.6s ease"}}>
        <span style={{fontFamily:FK,fontSize:"0.6rem",color:"rgba(255,255,255,0.3)",letterSpacing:"0.25em",textTransform:"uppercase"}}>60 exercises · 6 categories</span>
        <h1 style={{fontFamily:FS,fontSize:"clamp(1.6rem, 5vw, 2.4rem)",fontWeight:300,color:"rgba(255,255,255,0.88)",margin:"0.5rem 0 0.6rem"}}>Minutes of Mindfulness</h1>
        <button onClick={()=>{setActiveEx(EXERCISES[Math.floor(Math.random()*60)]);setSelCat(null);setShowFavs(false)}} style={{background:"rgba(167,199,168,0.08)",border:"1px solid rgba(167,199,168,0.15)",borderRadius:100,padding:"0.5rem 1.3rem",fontFamily:FK,fontSize:"0.7rem",color:"rgba(167,199,168,0.7)",cursor:"pointer"}}>🎲 Surprise me</button></div>
      <div style={{display:"flex",flexWrap:"wrap",justifyContent:"center",gap:"0.4rem",marginBottom:"1.5rem"}}>
        <button onClick={()=>{setSelCat(null);setShowFavs(false)}} style={{background:!selCat&&!showFavs?"rgba(255,255,255,0.08)":"rgba(255,255,255,0.03)",border:`1px solid ${!selCat&&!showFavs?"rgba(255,255,255,0.15)":"rgba(255,255,255,0.06)"}`,borderRadius:100,padding:"0.45rem 0.9rem",fontFamily:FK,fontSize:"0.65rem",color:!selCat&&!showFavs?"rgba(255,255,255,0.8)":"rgba(255,255,255,0.3)",cursor:"pointer"}}>All</button>
        {favourites.length>0&&<button onClick={()=>{setShowFavs(!showFavs);setSelCat(null)}} style={{background:showFavs?"rgba(210,170,180,0.1)":"rgba(255,255,255,0.03)",border:`1px solid ${showFavs?"rgba(210,170,180,0.2)":"rgba(255,255,255,0.06)"}`,borderRadius:100,padding:"0.45rem 0.9rem",fontFamily:FK,fontSize:"0.65rem",color:showFavs?"rgba(210,170,180,0.9)":"rgba(255,255,255,0.3)",cursor:"pointer"}}>♥ Favourites</button>}
        {CATEGORIES.map(c=>{const a=selCat===c.id;return(<button key={c.id} onClick={()=>{setSelCat(a?null:c.id);setShowFavs(false)}} style={{background:a?c.bg:"rgba(255,255,255,0.03)",border:`1px solid ${a?c.border:"rgba(255,255,255,0.06)"}`,borderRadius:100,padding:"0.45rem 0.9rem",fontFamily:FK,fontSize:"0.65rem",color:a?c.color:"rgba(255,255,255,0.3)",cursor:"pointer"}}>{c.icon} {c.name}</button>)})}</div>
      {selCat&&<p style={{textAlign:"center",fontFamily:FS,fontSize:"0.9rem",fontStyle:"italic",color:"rgba(255,255,255,0.35)",marginBottom:"1.5rem"}}>{CATEGORIES.find(c=>c.id===selCat)?.desc}</p>}
      <div style={{display:"grid",gridTemplateColumns:"repeat(auto-fill, minmax(280px, 1fr))",gap:"0.75rem",maxWidth:900,margin:"0 auto"}}>
        {display.map((ex,idx)=>{const cat=CATEGORIES.find(c=>c.id===ex.cat);const fav=favourites.includes(ex.id);return(
          <button key={ex.id} onClick={()=>setActiveEx(ex)} style={{background:"rgba(255,255,255,0.025)",border:"1px solid rgba(255,255,255,0.06)",borderRadius:14,padding:"1.1rem 1.25rem",cursor:"pointer",textAlign:"left",transition:"all 0.3s ease",animation:`fadeUp 0.5s ease ${idx*0.03}s both`,display:"flex",flexDirection:"column",gap:"0.5rem",position:"relative"}}
            onMouseEnter={e=>{e.currentTarget.style.background=cat.bg;e.currentTarget.style.borderColor=cat.border}}
            onMouseLeave={e=>{e.currentTarget.style.background="rgba(255,255,255,0.025)";e.currentTarget.style.borderColor="rgba(255,255,255,0.06)"}}>
            {fav&&<span style={{position:"absolute",top:10,right:12,fontSize:"0.6rem",color:"rgba(210,170,180,0.6)"}}>♥</span>}
            <div style={{display:"flex",justifyContent:"space-between",alignItems:"center"}}><span style={{fontFamily:FK,fontSize:"0.6rem",color:cat.color,letterSpacing:"0.12em",textTransform:"uppercase",opacity:0.7}}>{cat.icon} {cat.name}</span><span style={{fontFamily:FK,fontSize:"0.65rem",color:"rgba(255,255,255,0.25)"}}>{ex.mins} min</span></div>
            <span style={{fontFamily:FS,fontSize:"1.15rem",fontWeight:400,color:"rgba(255,255,255,0.8)"}}>{ex.name}</span>
            <span style={{fontFamily:FK,fontSize:"0.72rem",color:"rgba(255,255,255,0.3)",lineHeight:1.5}}>{ex.steps[0]}</span></button>)})}</div>
      {!selCat&&!showFavs&&<p style={{textAlign:"center",marginTop:"1.5rem",fontFamily:FK,fontSize:"0.7rem",color:"rgba(255,255,255,0.2)"}}>Choose a category to explore all 60 exercises</p>}</div>);}

// ============================================================
// MOOD TIMER
// ============================================================
function MoodTimer({session,mood,onClose}){
  const [phase,setPhase]=useState("ready");const [timeLeft,setTimeLeft]=useState(session.mins*60);const [step,setStep]=useState(0);
  const ref=useRef(null);const tt=session.mins*60;const sd=tt/session.steps.length;
  useEffect(()=>{if(phase==="active"){ref.current=setInterval(()=>{setTimeLeft(t=>{if(t<=1){clearInterval(ref.current);setPhase("complete");return 0}return t-1})},1000)}return()=>clearInterval(ref.current)},[phase]);
  useEffect(()=>{if(phase==="active")setStep(Math.min(Math.floor((tt-timeLeft)/sd),session.steps.length-1))},[timeLeft,phase,tt,sd,session.steps.length]);
  const progress=phase==="ready"?0:phase==="complete"?1:(tt-timeLeft)/tt;const m=Math.floor(timeLeft/60),s=timeLeft%60;
  return(
    <div style={{position:"fixed",inset:0,zIndex:150,background:"rgba(8,12,10,0.96)",display:"flex",flexDirection:"column",alignItems:"center",justifyContent:"center",padding:"2rem",animation:"fadeUp 0.5s ease"}}>
      <button onClick={()=>{clearInterval(ref.current);onClose()}} style={{position:"absolute",top:"1.5rem",right:"1.5rem",background:"none",border:"none",color:"rgba(255,255,255,0.3)",cursor:"pointer",fontSize:"1.5rem",padding:"0.5rem"}}>✕</button>
      <span style={{fontSize:"2rem",marginBottom:"0.5rem"}}>{mood.emoji}</span>
      <span style={{fontFamily:FK,fontSize:"0.65rem",color:mood.color,letterSpacing:"0.2em",textTransform:"uppercase",marginBottom:"0.3rem",opacity:0.7}}>{LEN_META[session.length].icon} {LEN_META[session.length].label}</span>
      <h2 style={{fontFamily:FS,fontSize:"1.5rem",fontWeight:300,color:"rgba(255,255,255,0.85)",marginBottom:"2.5rem"}}>{session.label}</h2>
      <div style={{position:"relative",width:200,height:200,marginBottom:"2.5rem"}}>
        <svg width="200" height="200" style={{transform:"rotate(-90deg)"}}><circle cx="100" cy="100" r="90" fill="none" stroke="rgba(255,255,255,0.05)" strokeWidth="3"/><circle cx="100" cy="100" r="90" fill="none" stroke={mood.color} strokeWidth="3" strokeLinecap="round" strokeDasharray={2*Math.PI*90} strokeDashoffset={2*Math.PI*90*(1-progress)} style={{transition:"stroke-dashoffset 1s linear",opacity:0.7}}/></svg>
        <div style={{position:"absolute",inset:0,display:"flex",flexDirection:"column",alignItems:"center",justifyContent:"center"}}>
          {phase==="complete"?<span style={{fontFamily:FS,fontSize:"1.3rem",color:mood.color}}>be well</span>:(<>
            <span style={{fontFamily:FK,fontSize:"2.8rem",fontWeight:300,color:"rgba(255,255,255,0.85)"}}>{m}:{String(s).padStart(2,"0")}</span>
            <span style={{fontFamily:FK,fontSize:"0.6rem",color:"rgba(255,255,255,0.3)",letterSpacing:"0.15em",textTransform:"uppercase"}}>{phase==="ready"?"ready":"remaining"}</span></>)}</div></div>
      <div style={{maxWidth:440,textAlign:"center",minHeight:90,display:"flex",alignItems:"center",justifyContent:"center",marginBottom:"2rem"}}>
        <p style={{fontFamily:FS,fontSize:"1.15rem",lineHeight:1.65,color:"rgba(255,255,255,0.65)",fontWeight:300,fontStyle:"italic"}}>{phase==="complete"?"You showed up for yourself.":session.steps[step]}</p></div>
      <div style={{display:"flex",gap:6,marginBottom:"2.5rem",flexWrap:"wrap",justifyContent:"center"}}>{session.steps.map((_,i)=><div key={i} style={{width:6,height:6,borderRadius:"50%",background:i<=step&&phase!=="ready"?mood.color:"rgba(255,255,255,0.1)"}}/>)}</div>
      {phase==="ready"&&<button onClick={()=>setPhase("active")} style={{background:mood.bg,border:`1px solid ${mood.border}`,borderRadius:100,padding:"0.85rem 2.5rem",fontFamily:FK,fontSize:"0.8rem",color:mood.color,letterSpacing:"0.15em",textTransform:"uppercase",cursor:"pointer"}}>Begin</button>}
      {phase==="active"&&<button onClick={()=>{clearInterval(ref.current);setPhase("ready");setTimeLeft(tt);setStep(0)}} style={{background:"rgba(255,255,255,0.04)",border:"1px solid rgba(255,255,255,0.1)",borderRadius:100,padding:"0.7rem 2rem",fontFamily:FK,fontSize:"0.75rem",color:"rgba(255,255,255,0.35)",cursor:"pointer"}}>Reset</button>}
      {phase==="complete"&&<button onClick={onClose} style={{background:mood.bg,border:`1px solid ${mood.border}`,borderRadius:100,padding:"0.85rem 2.5rem",fontFamily:FK,fontSize:"0.8rem",color:mood.color,letterSpacing:"0.15em",textTransform:"uppercase",cursor:"pointer"}}>Return</button>}</div>);}

// ============================================================
// MOOD BOARD PAGE (tracker + sessions + insights)
// ============================================================
function MoodBoardPage({navigateTo}){
  const [tab,setTab]=useState("sessions");const [selMood,setSelMood]=useState(null);const [activeSess,setActiveSess]=useState(null);
  const [logs,setLogs]=useState(()=>{const d={};const now=new Date();for(let i=13;i>=1;i--){const dt=new Date(now);dt.setDate(dt.getDate()-i);const k=dk(dt);d[k]={};const ms=["great","good","okay","low","rough"];if(Math.random()>0.15)d[k].morning=ms[Math.floor(Math.random()*5)];if(Math.random()>0.1)d[k].afternoon=ms[Math.floor(Math.random()*5)];if(Math.random()>0.2)d[k].evening=ms[Math.floor(Math.random()*5)];if(!Object.keys(d[k]).length)delete d[k]}return d});
  const [selDate,setSelDate]=useState(new Date());
  const mood=selMood?MOODS.find(m=>m.id===selMood):null;const todayKey=dk(selDate);const todayLogs=logs[todayKey]||{};const insights=genInsights(logs);
  const logMood=(slot,mid)=>setLogs(p=>({...p,[todayKey]:{...(p[todayKey]||{}),[slot]:mid}}));
  const weekData=[];for(let i=6;i>=0;i--){const d=new Date();d.setDate(d.getDate()-i);const k=dk(d);const dl=logs[k]||{};const sc=Object.values(dl).map(id=>TRACKER_MOODS.find(m=>m.id===id)?.score).filter(Boolean);weekData.push({day:dn(d),avg:sc.length?sc.reduce((a,b)=>a+b,0)/sc.length:null,date:d})}
  const tabS=a=>({background:a?"rgba(255,255,255,0.08)":"none",border:`1px solid ${a?"rgba(255,255,255,0.15)":"rgba(255,255,255,0.05)"}`,borderRadius:100,padding:"0.55rem 1.1rem",fontFamily:FK,fontSize:"0.7rem",color:a?"rgba(255,255,255,0.8)":"rgba(255,255,255,0.3)",cursor:"pointer",letterSpacing:"0.05em"});
  return(
    <div style={{minHeight:"100vh",background:"linear-gradient(180deg, #0e100f 0%, #0a0f0c 100%)",padding:"2rem 1.25rem"}}>
      {activeSess&&mood&&<MoodTimer session={activeSess} mood={mood} onClose={()=>setActiveSess(null)}/>}
      <div style={{textAlign:"center",marginBottom:"1.5rem",animation:"fadeUp 0.6s ease"}}><h1 style={{fontFamily:FS,fontSize:"clamp(1.6rem, 5vw, 2.4rem)",fontWeight:300,color:"rgba(255,255,255,0.88)",margin:"0 0 0.4rem"}}>Mood Board</h1>
        <p style={{fontFamily:FK,fontSize:"0.8rem",color:"rgba(255,255,255,0.3)",maxWidth:380,margin:"0 auto"}}>Track, discover patterns, find guided support.</p></div>
      <div style={{display:"flex",justifyContent:"center",gap:"0.5rem",marginBottom:"2rem"}}><button onClick={()=>{setTab("tracker");setSelMood(null)}} style={tabS(tab==="tracker")}>📊 Tracker</button><button onClick={()=>{setTab("sessions");setSelMood(null)}} style={tabS(tab==="sessions")}>🧘 Sessions</button><button onClick={()=>{setTab("insights");setSelMood(null)}} style={tabS(tab==="insights")}>💡 Insights</button></div>

      {tab==="tracker"&&(<div style={{maxWidth:600,margin:"0 auto",animation:"fadeUp 0.5s ease"}}>
        <div style={{display:"flex",alignItems:"center",justifyContent:"center",gap:"1.5rem",marginBottom:"2rem"}}><button onClick={()=>{const d=new Date(selDate);d.setDate(d.getDate()-1);setSelDate(d)}} style={{background:"none",border:"none",color:"rgba(255,255,255,0.3)",cursor:"pointer",fontSize:"1.2rem"}}>‹</button>
          <div style={{textAlign:"center"}}><span style={{fontFamily:FS,fontSize:"1.3rem",color:"rgba(255,255,255,0.85)",fontWeight:300}}>{todayKey===dk(new Date())?"Today":dn(selDate)+", "+selDate.getDate()+" "+mn(selDate)}</span>
            {todayKey!==dk(new Date())&&<button onClick={()=>setSelDate(new Date())} style={{background:"none",border:"none",fontFamily:FK,fontSize:"0.6rem",color:"rgba(167,199,168,0.6)",cursor:"pointer",display:"block",margin:"0.25rem auto 0"}}>Jump to today</button>}</div>
          <button onClick={()=>{const d=new Date(selDate);d.setDate(d.getDate()+1);if(d<=new Date())setSelDate(d)}} style={{background:"none",border:"none",color:todayKey===dk(new Date())?"rgba(255,255,255,0.1)":"rgba(255,255,255,0.3)",cursor:todayKey===dk(new Date())?"default":"pointer",fontSize:"1.2rem"}}>›</button></div>
        {TIME_SLOTS.map(slot=>{const lg=todayLogs[slot.id];const lm=lg?TRACKER_MOODS.find(m=>m.id===lg):null;return(
          <div key={slot.id} style={{background:"rgba(255,255,255,0.025)",border:"1px solid rgba(255,255,255,0.06)",borderRadius:16,padding:"1.25rem",marginBottom:"1rem"}}>
            <div style={{display:"flex",alignItems:"center",gap:"0.6rem",marginBottom:"1rem"}}><span style={{fontSize:"1.2rem"}}>{slot.icon}</span><div><span style={{fontFamily:FS,fontSize:"1.1rem",color:"rgba(255,255,255,0.8)"}}>{slot.label}</span><span style={{fontFamily:FK,fontSize:"0.6rem",color:"rgba(255,255,255,0.2)",display:"block"}}>{slot.hours}</span></div>
              {lm&&<span style={{marginLeft:"auto",fontFamily:FK,fontSize:"0.65rem",color:lm.color}}>{lm.emoji} {lm.label}</span>}</div>
            <div style={{display:"flex",gap:"0.5rem"}}>{TRACKER_MOODS.map(tm=>{const sel=lg===tm.id;return(<button key={tm.id} onClick={()=>logMood(slot.id,tm.id)} style={{flex:1,background:sel?"rgba(255,255,255,0.08)":"rgba(255,255,255,0.02)",border:`1px solid ${sel?tm.color.replace("0.9","0.4"):"rgba(255,255,255,0.05)"}`,borderRadius:12,padding:"0.6rem 0.25rem",cursor:"pointer",display:"flex",flexDirection:"column",alignItems:"center",gap:"0.25rem",transform:sel?"scale(1.05)":"scale(1)",transition:"all 0.2s ease"}}><span style={{fontSize:"1.3rem"}}>{tm.emoji}</span><span style={{fontFamily:FK,fontSize:"0.55rem",color:sel?tm.color:"rgba(255,255,255,0.25)"}}>{tm.label}</span></button>)})}</div></div>)})}
        <div style={{background:"rgba(255,255,255,0.025)",border:"1px solid rgba(255,255,255,0.06)",borderRadius:16,padding:"1.25rem"}}><span style={{fontFamily:FK,fontSize:"0.6rem",color:"rgba(255,255,255,0.3)",letterSpacing:"0.15em",textTransform:"uppercase",display:"block",marginBottom:"1rem"}}>This Week</span>
          <div style={{display:"flex",alignItems:"flex-end",gap:"0.5rem",height:80}}>{weekData.map((d,i)=>{const h=d.avg?(d.avg/5)*100:0;const sm=d.avg?scoreToMood(d.avg):null;return(<div key={i} style={{flex:1,display:"flex",flexDirection:"column",alignItems:"center",gap:"0.3rem"}}><div style={{width:"100%",height:60,display:"flex",alignItems:"flex-end",justifyContent:"center"}}>{d.avg?<div style={{width:"100%",maxWidth:32,height:`${h*0.6}%`,minHeight:8,background:sm.color.replace("0.9","0.3"),borderRadius:"6px 6px 2px 2px"}}/>:<div style={{width:"100%",maxWidth:32,height:4,background:"rgba(255,255,255,0.05)",borderRadius:2}}/>}</div><span style={{fontFamily:FK,fontSize:"0.55rem",color:dk(d.date)===todayKey?"rgba(167,199,168,0.8)":"rgba(255,255,255,0.25)"}}>{d.day}</span></div>)})}</div></div></div>)}

      {tab==="sessions"&&!selMood&&(<div style={{display:"grid",gridTemplateColumns:"repeat(auto-fill, minmax(150px, 1fr))",gap:"0.75rem",maxWidth:700,margin:"0 auto",animation:"fadeUp 0.5s ease"}}>{MOODS.map((m,idx)=>(<button key={m.id} onClick={()=>setSelMood(m.id)} style={{background:m.bg,border:`1px solid ${m.border}`,borderRadius:16,padding:"1.5rem 1rem",cursor:"pointer",textAlign:"center",transition:"all 0.3s ease",animation:`fadeUp 0.5s ease ${idx*0.05}s both`,display:"flex",flexDirection:"column",alignItems:"center",gap:"0.6rem"}} onMouseEnter={e=>{e.currentTarget.style.background=m.bg.replace("0.08","0.15");e.currentTarget.style.transform="translateY(-2px)"}} onMouseLeave={e=>{e.currentTarget.style.background=m.bg;e.currentTarget.style.transform="translateY(0)"}}><span style={{fontSize:"2rem"}}>{m.emoji}</span><span style={{fontFamily:FS,fontSize:"1.1rem",color:m.color}}>{m.name}</span></button>))}</div>)}
      {tab==="sessions"&&selMood&&(<div style={{maxWidth:600,margin:"0 auto",animation:"fadeUp 0.5s ease"}}><button onClick={()=>setSelMood(null)} style={{background:"none",border:"none",color:"rgba(255,255,255,0.3)",cursor:"pointer",fontFamily:FK,fontSize:"0.75rem",display:"flex",alignItems:"center",gap:"0.4rem",marginBottom:"1.5rem",padding:0}}>← All moods</button>
        <div style={{textAlign:"center",marginBottom:"2rem"}}><span style={{fontSize:"2.5rem",display:"block",marginBottom:"0.75rem"}}>{mood.emoji}</span><h2 style={{fontFamily:FS,fontSize:"1.8rem",fontWeight:300,color:mood.color,marginBottom:"0.75rem"}}>Feeling {mood.name}</h2><p style={{fontFamily:FS,fontSize:"1rem",color:"rgba(255,255,255,0.4)",fontStyle:"italic",maxWidth:400,margin:"0 auto"}}>{mood.message}</p></div>
        {mood.sessions.map((sess,idx)=>{const meta=LEN_META[sess.length];return(<button key={sess.id} onClick={()=>setActiveSess(sess)} style={{width:"100%",background:"rgba(255,255,255,0.025)",border:"1px solid rgba(255,255,255,0.06)",borderRadius:16,padding:"1.25rem 1.5rem",cursor:"pointer",textAlign:"left",transition:"all 0.3s ease",animation:`fadeUp 0.5s ease ${idx*0.1}s both`,display:"flex",alignItems:"center",gap:"1.25rem",marginBottom:"0.75rem"}} onMouseEnter={e=>{e.currentTarget.style.background=mood.bg;e.currentTarget.style.borderColor=mood.border}} onMouseLeave={e=>{e.currentTarget.style.background="rgba(255,255,255,0.025)";e.currentTarget.style.borderColor="rgba(255,255,255,0.06)"}}><div style={{width:48,height:48,borderRadius:"50%",background:mood.bg,border:`1px solid ${mood.border}`,display:"flex",alignItems:"center",justifyContent:"center",fontSize:"1.2rem",flexShrink:0}}>{meta.icon}</div><div style={{flex:1}}><div style={{display:"flex",justifyContent:"space-between",marginBottom:"0.3rem"}}><span style={{fontFamily:FS,fontSize:"1.15rem",color:"rgba(255,255,255,0.8)"}}>{sess.label}</span><span style={{fontFamily:FK,fontSize:"0.65rem",color:"rgba(255,255,255,0.25)"}}>{sess.mins} min</span></div><span style={{fontFamily:FK,fontSize:"0.65rem",color:mood.color,letterSpacing:"0.1em",textTransform:"uppercase",opacity:0.7}}>{meta.label}</span><p style={{fontFamily:FK,fontSize:"0.72rem",color:"rgba(255,255,255,0.3)",lineHeight:1.5,marginTop:"0.4rem"}}>{sess.steps[0]}</p></div></button>)})}</div>)}

      {tab==="insights"&&(<div style={{maxWidth:600,margin:"0 auto",animation:"fadeUp 0.5s ease"}}>
        <div style={{display:"grid",gridTemplateColumns:"repeat(2, 1fr)",gap:"0.75rem",marginBottom:"1.5rem"}}>{[{l:"Weekly Avg",v:insights.weekAvg,m:true},{l:"Monthly Avg",v:insights.monthAvg,m:true},{l:"Streak",v:insights.streak?`${insights.streak}d`:"Start",m:false},{l:"Check-ins",v:insights.totalLogs||0,m:false}].map((st,i)=>{const sm=st.m?scoreToMood(st.v):null;return(<div key={i} style={{background:"rgba(255,255,255,0.025)",border:"1px solid rgba(255,255,255,0.06)",borderRadius:16,padding:"1.25rem",textAlign:"center",animation:`fadeUp 0.5s ease ${i*0.08}s both`}}><span style={{fontFamily:FK,fontSize:"0.55rem",color:"rgba(255,255,255,0.3)",letterSpacing:"0.15em",textTransform:"uppercase",display:"block",marginBottom:"0.6rem"}}>{st.l}</span>{st.m?(<div><span style={{fontSize:"1.5rem"}}>{sm.emoji}</span><span style={{fontFamily:FK,fontSize:"0.7rem",color:sm.color,display:"block",marginTop:"0.3rem"}}>{sm.label}</span>{st.v!==null&&<span style={{fontFamily:FK,fontSize:"0.6rem",color:"rgba(255,255,255,0.2)"}}>{st.v.toFixed(1)}/5</span>}</div>):(<span style={{fontFamily:FS,fontSize:"1.4rem",color:"rgba(255,255,255,0.8)",fontWeight:300}}>{st.v}</span>)}</div>)})}</div>
        {insights.trend&&<div style={{background:"rgba(255,255,255,0.025)",border:"1px solid rgba(255,255,255,0.06)",borderRadius:16,padding:"1rem 1.25rem",marginBottom:"1.5rem",display:"flex",alignItems:"center",gap:"1rem"}}><span style={{fontSize:"1.5rem"}}>{insights.trend==="improving"?"📈":insights.trend==="declining"?"📉":"➡️"}</span><div><span style={{fontFamily:FK,fontSize:"0.6rem",color:"rgba(255,255,255,0.3)",letterSpacing:"0.15em",textTransform:"uppercase",display:"block",marginBottom:"0.2rem"}}>Trend</span><span style={{fontFamily:FS,fontSize:"1.1rem",color:insights.trend==="improving"?"rgba(150,200,150,0.9)":insights.trend==="declining"?"rgba(210,150,150,0.9)":"rgba(200,190,130,0.9)",fontWeight:300}}>{insights.trend==="improving"?"Mood improving":insights.trend==="declining"?"Mood dipping":"Mood steady"}</span></div></div>}
        {insights.recommendations.length>0&&<div><span style={{fontFamily:FK,fontSize:"0.55rem",color:"rgba(255,255,255,0.3)",letterSpacing:"0.15em",textTransform:"uppercase",display:"block",marginBottom:"0.75rem"}}>Recommendations</span>{insights.recommendations.map((r,i)=>(<div key={i} style={{background:"rgba(255,255,255,0.025)",border:"1px solid rgba(255,255,255,0.06)",borderRadius:14,padding:"1rem 1.25rem",display:"flex",alignItems:"flex-start",gap:"0.75rem",marginBottom:"0.6rem"}}><span style={{fontSize:"1.1rem",flexShrink:0}}>{r.icon}</span><div style={{flex:1}}><p style={{fontFamily:FK,fontSize:"0.8rem",color:"rgba(255,255,255,0.6)",lineHeight:1.6,margin:0}}>{r.text}</p>{r.act&&<button onClick={()=>{if(r.act==="sessions")setTab("sessions");else if(navigateTo)navigateTo(r.act)}} style={{background:"rgba(167,199,168,0.08)",border:"1px solid rgba(167,199,168,0.15)",borderRadius:100,padding:"0.4rem 1rem",marginTop:"0.6rem",fontFamily:FK,fontSize:"0.65rem",color:"rgba(167,199,168,0.8)",cursor:"pointer"}}>Go →</button>}</div></div>))}</div>}
      </div>)}
    </div>);}


// ============================================================
// GUIDED ESCAPES PAGE (soundscape mixer + narrative journeys)
// ============================================================
function GuidedEscapesPage(){
  const [tab,setTab]=useState("sounds");
  const [volumes,setVolumes]=useState({});
  const [sleepTimer,setSleepTimer]=useState(null);
  const [sleepLeft,setSleepLeft]=useState(null);
  const [activeJourney,setActiveJourney]=useState(null);
  const [jPhase,setJPhase]=useState("preview");
  const [jTime,setJTime]=useState(0);
  const [jStep,setJStep]=useState(0);
  const jRef=useRef(null);
  const audioCtxRef=useRef(null);
  const nodesRef=useRef({});

  // Initialize AudioContext on first interaction
  const getCtx=useCallback(()=>{
    if(!audioCtxRef.current){audioCtxRef.current=new (window.AudioContext||window.webkitAudioContext)()}
    if(audioCtxRef.current.state==="suspended")audioCtxRef.current.resume();
    return audioCtxRef.current;
  },[]);

  // Create noise buffers of different colours
  const makeNoise=(ctx,type,seconds=4)=>{
    const sr=ctx.sampleRate,len=sr*seconds;
    const buf=ctx.createBuffer(2,len,sr);
    for(let ch=0;ch<2;ch++){
      const d=buf.getChannelData(ch);
      let b0=0,b1=0,b2=0,b3=0,b4=0,b5=0,b6=0,last=0;
      for(let i=0;i<len;i++){
        const white=Math.random()*2-1;
        if(type==="pink"){b0=0.99886*b0+white*0.0555179;b1=0.99332*b1+white*0.0750759;b2=0.969*b2+white*0.153852;b3=0.8665*b3+white*0.3104856;b4=0.55*b4+white*0.5329522;b5=-0.7616*b5-white*0.016898;d[i]=(b0+b1+b2+b3+b4+b5+b6+white*0.5362)*0.11;b6=white*0.115926}
        else if(type==="brown"){last=(last+(0.02*white))/1.02;d[i]=last*3.5}
        else if(type==="white"){d[i]=white*0.5}
        else if(type==="velvet"){d[i]=Math.random()<0.05?(Math.random()*2-1)*0.8:0}  // sparse impulse noise
        else{d[i]=white*0.5}
      }
    }return buf;
  };

  // Create ambient sound nodes - multi-layer for each soundscape type
  const createSoundNodes=(ctx,id)=>{
    const master=ctx.createGain();master.gain.value=0;master.connect(ctx.destination);
    const sources=[];const extra=[];

    // Helper: create a filtered noise layer
    const addLayer=(noiseType,filterType,freq,Q,gainVal,panVal)=>{
      const src=ctx.createBufferSource();src.buffer=makeNoise(ctx,noiseType);src.loop=true;
      const filt=ctx.createBiquadFilter();filt.type=filterType;filt.frequency.value=freq;filt.Q.value=Q;
      const g=ctx.createGain();g.gain.value=gainVal;
      const pan=ctx.createStereoPanner();pan.pan.value=panVal||0;
      src.connect(filt);filt.connect(g);g.connect(pan);pan.connect(master);
      src.start(0,Math.random()*3);sources.push(src);
      return{src,filt,gain:g,pan};
    };

    // Helper: create LFO modulating a parameter
    const addLFO=(param,rate,depth)=>{
      const lfo=ctx.createOscillator();const lg=ctx.createGain();
      lfo.frequency.value=rate;lg.gain.value=depth;
      lfo.connect(lg);lg.connect(param);lfo.start();extra.push(lfo);
    };

    switch(id){
      case "rain":{
        // Rain: pink noise high-shelf + brown noise low rumble + velvet crackle layer
        const hi=addLayer("pink","highpass",2000,0.3,0.6,0);
        const mid=addLayer("pink","bandpass",4000,0.4,0.25,-0.3);
        const low=addLayer("brown","lowpass",300,0.5,0.2,0.2);
        const crackle=addLayer("velvet","highpass",3000,1,0.15,0);
        // Gentle volume swell
        addLFO(hi.gain.gain,0.07,0.08);
        addLFO(mid.gain.gain,0.11,0.05);
        break;
      }
      case "forest":{
        // Forest: gentle pink noise + bird-like chirps via modulated bandpass + rustling
        const base=addLayer("pink","bandpass",800,0.4,0.15,0);
        const rustle=addLayer("white","bandpass",3500,2,0.06,-0.4);
        const rustle2=addLayer("white","bandpass",5000,2,0.04,0.5);
        // Slow swelling for "wind through leaves"
        addLFO(base.filt.frequency,0.04,300);
        addLFO(rustle.gain.gain,0.15,0.04);
        addLFO(rustle2.gain.gain,0.22,0.03);
        // Birdsong: high resonant sweeping
        const bird=addLayer("velvet","bandpass",3000,8,0.08,0.3);
        const bird2=addLayer("velvet","bandpass",4500,6,0.06,-0.5);
        addLFO(bird.filt.frequency,1.7,1500);
        addLFO(bird2.filt.frequency,2.3,1200);
        break;
      }
      case "ocean":{
        // Ocean: brown noise with slow LFO for wave surge + white wash on top
        const surge=addLayer("brown","lowpass",250,0.2,0.6,0);
        const mid=addLayer("brown","bandpass",400,0.3,0.2,0);
        const wash=addLayer("pink","highpass",1500,0.3,0.12,0);
        const foam=addLayer("white","highpass",4000,0.5,0.05,0.3);
        // Slow wave cycle ~8 seconds
        addLFO(surge.filt.frequency,0.125,200);
        addLFO(surge.gain.gain,0.125,0.15);
        addLFO(wash.gain.gain,0.125,0.08);
        addLFO(mid.gain.gain,0.08,0.06);
        break;
      }
      case "fire":{
        // Fire: brown crackle + mid pop + low rumble
        const crackle=addLayer("velvet","bandpass",2000,3,0.35,0);
        const crackle2=addLayer("velvet","bandpass",800,2,0.2,-0.3);
        const rumble=addLayer("brown","lowpass",200,0.5,0.25,0);
        const hiss=addLayer("pink","highpass",3000,1,0.06,0.2);
        // Fast flutter for crackling
        addLFO(crackle.gain.gain,4,0.15);
        addLFO(crackle2.gain.gain,2.5,0.1);
        addLFO(rumble.gain.gain,0.3,0.06);
        addLFO(crackle.filt.frequency,6,600);
        break;
      }
      case "wind":{
        // Wind: layered brown noise at different bands with very slow LFOs
        const low=addLayer("brown","lowpass",250,0.15,0.35,0);
        const mid=addLayer("brown","bandpass",600,0.3,0.2,-0.2);
        const hi=addLayer("pink","bandpass",1500,0.5,0.08,0.3);
        const whistle=addLayer("white","bandpass",3000,4,0.03,0.5);
        // Very slow swell (15-20 second gusts)
        addLFO(low.gain.gain,0.05,0.15);
        addLFO(mid.gain.gain,0.07,0.1);
        addLFO(low.filt.frequency,0.03,150);
        addLFO(whistle.gain.gain,0.04,0.02);
        break;
      }
      case "birds":{
        // Birds: multiple narrow chirp bands via velvet noise + gentle pink background
        const amb=addLayer("pink","bandpass",1000,0.5,0.06,0);
        const c1=addLayer("velvet","bandpass",2800,10,0.12,0.4);
        const c2=addLayer("velvet","bandpass",4200,8,0.1,-0.3);
        const c3=addLayer("velvet","bandpass",3500,12,0.08,0.6);
        const c4=addLayer("velvet","bandpass",5000,6,0.06,-0.6);
        // Each "bird" sweeps at different rates
        addLFO(c1.filt.frequency,1.5,1000);
        addLFO(c2.filt.frequency,2.1,800);
        addLFO(c3.filt.frequency,3.2,1200);
        addLFO(c4.filt.frequency,0.8,600);
        // Intermittent volume
        addLFO(c1.gain.gain,0.3,0.06);
        addLFO(c3.gain.gain,0.5,0.04);
        break;
      }
      case "bowls":{
        // Singing bowls: pure tones at harmonic intervals + shimmer
        const freqs=[174,285,396,528,639];
        freqs.forEach((f,i)=>{
          const osc=ctx.createOscillator();osc.type="sine";osc.frequency.value=f;
          const g=ctx.createGain();g.gain.value=0.06-i*0.008;
          const pan=ctx.createStereoPanner();pan.pan.value=(i-2)*0.3;
          osc.connect(g);g.connect(pan);pan.connect(master);
          osc.start();extra.push(osc);
          // Slow beating via micro-detuning
          addLFO(osc.frequency,0.1+i*0.05,0.5);
          addLFO(g.gain,0.08+i*0.03,0.015);
        });
        // Shimmering overtone layer
        const shimmer=addLayer("pink","bandpass",800,6,0.03,0);
        addLFO(shimmer.filt.frequency,0.2,200);
        break;
      }
      case "stream":{
        // Stream: pink mid-high babble + brown low flow + white splash
        const flow=addLayer("brown","lowpass",400,0.3,0.2,0);
        const babble=addLayer("pink","bandpass",2500,1,0.25,0.2);
        const babble2=addLayer("pink","bandpass",3500,1.5,0.15,-0.3);
        const splash=addLayer("white","highpass",5000,1,0.04,0.4);
        addLFO(babble.gain.gain,0.25,0.08);
        addLFO(babble2.gain.gain,0.35,0.06);
        addLFO(babble.filt.frequency,0.18,400);
        addLFO(flow.gain.gain,0.1,0.05);
        break;
      }
      default:{
        const base=addLayer("pink","lowpass",1000,0.5,0.3,0);
        addLFO(base.gain.gain,0.1,0.05);
      }
    }
    return{gain:master,sources,extra};
  };

  // Sync audio nodes with volumes state
  useEffect(()=>{
    const ctx=audioCtxRef.current;if(!ctx)return;
    // Remove nodes for sounds that were turned off
    Object.keys(nodesRef.current).forEach(id=>{
      if(volumes[id]===undefined){
        const n=nodesRef.current[id];
        n.gain.gain.linearRampToValueAtTime(0,ctx.currentTime+0.5);
        setTimeout(()=>{try{n.sources?.forEach(s=>s.stop());n.extra?.forEach(e=>{try{e.stop()}catch{}})}catch{}},600);
        delete nodesRef.current[id];
      }
    });
    // Create/update nodes for active sounds
    Object.entries(volumes).forEach(([id,vol])=>{
      if(!nodesRef.current[id]){
        nodesRef.current[id]=createSoundNodes(ctx,id);
      }
      nodesRef.current[id].gain.gain.linearRampToValueAtTime(vol*0.5,ctx.currentTime+0.15);
    });
  },[volumes]);

  // Cleanup on unmount
  useEffect(()=>()=>{Object.values(nodesRef.current).forEach(n=>{try{n.sources?.forEach(s=>s.stop());n.extra?.forEach(e=>{try{e.stop()}catch{}})}catch{}});nodesRef.current={};if(audioCtxRef.current)audioCtxRef.current.close()},[]);

  // Sleep timer countdown
  useEffect(()=>{if(sleepLeft===null)return;if(sleepLeft<=0){setVolumes({});setSleepLeft(null);setSleepTimer(null);return}
    const t=setTimeout(()=>setSleepLeft(s=>s-1),1000);return()=>clearTimeout(t)},[sleepLeft]);

  // Journey timer
  useEffect(()=>{if(jPhase==="active"&&activeJourney){jRef.current=setInterval(()=>{setJTime(t=>{const total=activeJourney.mins*60;if(t>=total-1){clearInterval(jRef.current);setJPhase("complete");return total}return t+1})},1000)}return()=>clearInterval(jRef.current)},[jPhase,activeJourney]);
  useEffect(()=>{if(jPhase==="active"&&activeJourney){const sd=activeJourney.mins*60/activeJourney.steps.length;setJStep(Math.min(Math.floor(jTime/sd),activeJourney.steps.length-1))}},[jTime,jPhase,activeJourney]);

  const toggleSound=(id)=>{getCtx();setVolumes(p=>{const n={...p};if(n[id])delete n[id];else n[id]=0.7;return n})};
  const setVol=(id,v)=>setVolumes(p=>({...p,[id]:v}));
  const activeSounds=Object.keys(volumes);
  const ts=a=>({background:a?"rgba(255,255,255,0.08)":"none",border:`1px solid ${a?"rgba(255,255,255,0.15)":"rgba(255,255,255,0.05)"}`,borderRadius:100,padding:"0.55rem 1.1rem",fontFamily:FK,fontSize:"0.7rem",color:a?"rgba(255,255,255,0.8)":"rgba(255,255,255,0.3)",cursor:"pointer",transition:"all 0.3s ease",letterSpacing:"0.05em"});

  // Journey overlay
  if(activeJourney&&jPhase!=="preview"){
    const j=activeJourney;const progress=jPhase==="complete"?1:jTime/(j.mins*60);const left=j.mins*60-jTime;const m=Math.floor(left/60),s=left%60;
    return(
      <div style={{position:"fixed",inset:0,zIndex:150,background:"rgba(8,12,10,0.97)",display:"flex",flexDirection:"column",alignItems:"center",justifyContent:"center",padding:"2rem",animation:"fadeUp 0.5s ease"}}>
        <button onClick={()=>{clearInterval(jRef.current);setActiveJourney(null);setJPhase("preview");setJTime(0);setJStep(0)}} style={{position:"absolute",top:"1.5rem",right:"1.5rem",background:"none",border:"none",color:"rgba(255,255,255,0.3)",cursor:"pointer",fontSize:"1.5rem",padding:"0.5rem"}}>✕</button>
        <span style={{fontSize:"2rem",marginBottom:"0.5rem"}}>{j.icon}</span>
        <h2 style={{fontFamily:FS,fontSize:"1.5rem",fontWeight:300,color:"rgba(255,255,255,0.85)",marginBottom:"1.5rem"}}>{j.name}</h2>
        <div style={{position:"relative",width:160,height:160,marginBottom:"2rem"}}>
          <svg width="160" height="160" style={{transform:"rotate(-90deg)"}}><circle cx="80" cy="80" r="72" fill="none" stroke="rgba(255,255,255,0.05)" strokeWidth="3"/><circle cx="80" cy="80" r="72" fill="none" stroke={j.color} strokeWidth="3" strokeLinecap="round" strokeDasharray={2*Math.PI*72} strokeDashoffset={2*Math.PI*72*(1-progress)} style={{transition:"stroke-dashoffset 1s linear",opacity:0.7}}/></svg>
          <div style={{position:"absolute",inset:0,display:"flex",flexDirection:"column",alignItems:"center",justifyContent:"center"}}>
            {jPhase==="complete"?<span style={{fontFamily:FS,fontSize:"1.2rem",color:j.color}}>peace</span>:(<>
              <span style={{fontFamily:FK,fontSize:"2rem",fontWeight:300,color:"rgba(255,255,255,0.85)"}}>{m}:{String(s).padStart(2,"0")}</span></>)}
          </div></div>
        <div style={{maxWidth:440,textAlign:"center",minHeight:80,display:"flex",alignItems:"center",justifyContent:"center",marginBottom:"2rem"}}>
          <p style={{fontFamily:FS,fontSize:"1.1rem",lineHeight:1.65,color:"rgba(255,255,255,0.6)",fontWeight:300,fontStyle:"italic"}}>{jPhase==="complete"?"You have returned, carrying peace within you.":j.steps[jStep]}</p></div>
        <div style={{display:"flex",gap:6,marginBottom:"2rem",flexWrap:"wrap",justifyContent:"center"}}>{j.steps.map((_,i)=><div key={i} style={{width:6,height:6,borderRadius:"50%",background:i<=jStep&&jPhase==="active"?j.color:"rgba(255,255,255,0.1)"}}/>)}</div>
        {jPhase==="ready"&&<button onClick={()=>{setJTime(0);setJStep(0);setJPhase("active")}} style={{background:"rgba(167,199,168,0.08)",border:"1px solid rgba(167,199,168,0.15)",borderRadius:100,padding:"0.85rem 2.5rem",fontFamily:FK,fontSize:"0.8rem",color:"rgba(167,199,168,0.8)",letterSpacing:"0.15em",textTransform:"uppercase",cursor:"pointer"}}>Begin Journey</button>}
        {jPhase==="complete"&&<button onClick={()=>{setActiveJourney(null);setJPhase("preview");setJTime(0);setJStep(0)}} style={{background:"rgba(167,199,168,0.08)",border:"1px solid rgba(167,199,168,0.15)",borderRadius:100,padding:"0.85rem 2.5rem",fontFamily:FK,fontSize:"0.8rem",color:"rgba(167,199,168,0.8)",letterSpacing:"0.15em",textTransform:"uppercase",cursor:"pointer"}}>Return</button>}
      </div>);}

  return(
    <div style={{minHeight:"100vh",background:"linear-gradient(165deg, #0e1214 0%, #0a0f0c 40%, #101418 100%)",padding:"2rem 1.25rem"}}>
      <div style={{textAlign:"center",marginBottom:"1.5rem",animation:"fadeUp 0.6s ease"}}>
        <h1 style={{fontFamily:FS,fontSize:"clamp(1.6rem, 5vw, 2.4rem)",fontWeight:300,color:"rgba(255,255,255,0.88)",margin:"0 0 0.4rem"}}>Guided Escapes</h1>
        <p style={{fontFamily:FK,fontSize:"0.8rem",color:"rgba(255,255,255,0.3)",maxWidth:380,margin:"0 auto",lineHeight:1.6}}>Mix ambient sounds or follow a guided journey.</p></div>
      <div style={{display:"flex",justifyContent:"center",gap:"0.5rem",marginBottom:"2rem"}}>
        <button onClick={()=>setTab("sounds")} style={ts(tab==="sounds")}>🎧 Soundscapes</button>
        <button onClick={()=>setTab("journeys")} style={ts(tab==="journeys")}>🌿 Journeys</button>
      </div>

      {/* SOUNDSCAPES */}
      {tab==="sounds"&&(<div style={{maxWidth:600,margin:"0 auto"}}>
        <div style={{display:"grid",gridTemplateColumns:"repeat(auto-fill, minmax(140px, 1fr))",gap:"0.75rem",marginBottom:"2rem"}}>
          {SOUNDSCAPES.map((s,idx)=>{const on=volumes[s.id]!==undefined;return(
            <button key={s.id} onClick={()=>toggleSound(s.id)} style={{background:on?"rgba(255,255,255,0.06)":"rgba(255,255,255,0.02)",border:`1px solid ${on?"rgba(255,255,255,0.12)":"rgba(255,255,255,0.05)"}`,borderRadius:16,padding:"1.25rem 0.75rem",cursor:"pointer",textAlign:"center",transition:"all 0.3s ease",animation:`fadeUp 0.5s ease ${idx*0.05}s both`}}>
              <span style={{fontSize:"1.8rem",display:"block",marginBottom:"0.5rem",filter:on?"none":"grayscale(0.5) opacity(0.5)"}}>{s.icon}</span>
              <span style={{fontFamily:FK,fontSize:"0.75rem",color:on?s.color:"rgba(255,255,255,0.3)"}}>{s.name}</span>
            </button>)})}
        </div>

        {/* Volume sliders for active sounds */}
        {activeSounds.length>0&&(<div style={{background:"rgba(255,255,255,0.025)",border:"1px solid rgba(255,255,255,0.06)",borderRadius:16,padding:"1.5rem",marginBottom:"1.5rem"}}>
          <span style={{fontFamily:FK,fontSize:"0.6rem",color:"rgba(255,255,255,0.3)",letterSpacing:"0.15em",textTransform:"uppercase",display:"block",marginBottom:"1rem"}}>Now Playing · {activeSounds.length} sound{activeSounds.length>1?"s":""}</span>
          {activeSounds.map(id=>{const s=SOUNDSCAPES.find(x=>x.id===id);return(
            <div key={id} style={{display:"flex",alignItems:"center",gap:"0.75rem",marginBottom:"0.75rem"}}>
              <span style={{fontSize:"1.1rem",width:28,textAlign:"center"}}>{s.icon}</span>
              <span style={{fontFamily:FK,fontSize:"0.75rem",color:s.color,width:90}}>{s.name}</span>
              <input type="range" min="0" max="100" value={(volumes[id]||0)*100} onChange={e=>setVol(id,e.target.value/100)} style={{flex:1,accentColor:s.color,height:4,opacity:0.7}}/>
              <span style={{fontFamily:FK,fontSize:"0.6rem",color:"rgba(255,255,255,0.25)",width:32,textAlign:"right"}}>{Math.round((volumes[id]||0)*100)}%</span>
            </div>)})}
          <div style={{marginTop:"1.25rem",paddingTop:"1rem",borderTop:"1px solid rgba(255,255,255,0.06)"}}>
            <span style={{fontFamily:FK,fontSize:"0.6rem",color:"rgba(255,255,255,0.3)",letterSpacing:"0.15em",textTransform:"uppercase",display:"block",marginBottom:"0.75rem"}}>Sleep Timer</span>
            <div style={{display:"flex",gap:"0.4rem",flexWrap:"wrap"}}>
              {[{l:"15m",v:15},{l:"30m",v:30},{l:"60m",v:60},{l:"Off",v:null}].map(t=>{const a=sleepTimer===t.v;return(
                <button key={t.l} onClick={()=>{setSleepTimer(t.v);setSleepLeft(t.v?t.v*60:null)}} style={{background:a?"rgba(167,199,168,0.1)":"rgba(255,255,255,0.03)",border:`1px solid ${a?"rgba(167,199,168,0.2)":"rgba(255,255,255,0.06)"}`,borderRadius:100,padding:"0.4rem 0.9rem",fontFamily:FK,fontSize:"0.65rem",color:a?"rgba(167,199,168,0.8)":"rgba(255,255,255,0.3)",cursor:"pointer"}}>{t.l}</button>)})}
              {sleepLeft!==null&&<span style={{fontFamily:FK,fontSize:"0.6rem",color:"rgba(167,199,168,0.5)",alignSelf:"center",marginLeft:"0.5rem"}}>{Math.floor(sleepLeft/60)}:{String(sleepLeft%60).padStart(2,"0")} left</span>}
            </div>
          </div>
        </div>)}
        {activeSounds.length===0&&<p style={{textAlign:"center",fontFamily:FK,fontSize:"0.75rem",color:"rgba(255,255,255,0.2)",marginTop:"1rem"}}>Tap sounds to create your mix</p>}
      </div>)}

      {/* JOURNEYS */}
      {tab==="journeys"&&(<div style={{maxWidth:600,margin:"0 auto"}}>
        {ESCAPE_JOURNEYS.map((j,idx)=>(
          <div key={j.id} style={{background:"rgba(255,255,255,0.025)",border:"1px solid rgba(255,255,255,0.06)",borderRadius:16,padding:"1.5rem",marginBottom:"0.75rem",animation:`fadeUp 0.5s ease ${idx*0.1}s both`}}>
            {activeJourney?.id===j.id&&jPhase==="preview"?(
              <div>
                <div style={{display:"flex",alignItems:"center",gap:"0.75rem",marginBottom:"1rem"}}>
                  <span style={{fontSize:"1.5rem"}}>{j.icon}</span>
                  <div><span style={{fontFamily:FS,fontSize:"1.2rem",color:"rgba(255,255,255,0.85)"}}>{j.name}</span>
                    <span style={{fontFamily:FK,fontSize:"0.65rem",color:"rgba(255,255,255,0.25)",display:"block"}}>{j.mins} min · {j.steps.length} moments</span></div></div>
                <div style={{marginBottom:"1.25rem"}}>
                  {j.steps.map((st,i)=>(<div key={i} style={{display:"flex",gap:"0.75rem",alignItems:"flex-start",marginBottom:"0.5rem",animation:`fadeUp 0.3s ease ${i*0.05}s both`}}>
                    <div style={{width:22,height:22,borderRadius:"50%",background:"rgba(255,255,255,0.04)",border:"1px solid rgba(255,255,255,0.08)",display:"flex",alignItems:"center",justifyContent:"center",fontFamily:FK,fontSize:"0.55rem",color:"rgba(255,255,255,0.25)",flexShrink:0}}>{i+1}</div>
                    <p style={{fontFamily:FK,fontSize:"0.8rem",color:"rgba(255,255,255,0.5)",lineHeight:1.5,margin:0}}>{st}</p></div>))}</div>
                <div style={{display:"flex",gap:"0.75rem"}}>
                  <button onClick={()=>{setActiveJourney(null);setJPhase("preview")}} style={{background:"rgba(255,255,255,0.04)",border:"1px solid rgba(255,255,255,0.1)",borderRadius:100,padding:"0.6rem 1.2rem",fontFamily:FK,fontSize:"0.7rem",color:"rgba(255,255,255,0.35)",cursor:"pointer"}}>Back</button>
                  <button onClick={()=>setJPhase("ready")} style={{background:"rgba(167,199,168,0.08)",border:"1px solid rgba(167,199,168,0.15)",borderRadius:100,padding:"0.6rem 1.5rem",fontFamily:FK,fontSize:"0.7rem",color:"rgba(167,199,168,0.8)",cursor:"pointer"}}>I'm Ready</button></div>
              </div>
            ):(
              <button onClick={()=>{setActiveJourney(j);setJPhase("preview");setJTime(0);setJStep(0)}} style={{background:"none",border:"none",cursor:"pointer",width:"100%",textAlign:"left",display:"flex",alignItems:"center",gap:"1rem"}}>
                <span style={{fontSize:"2rem"}}>{j.icon}</span>
                <div><span style={{fontFamily:FS,fontSize:"1.15rem",color:"rgba(255,255,255,0.8)",display:"block"}}>{j.name}</span>
                  <span style={{fontFamily:FK,fontSize:"0.65rem",color:"rgba(255,255,255,0.25)"}}>{j.mins} min journey</span>
                  <p style={{fontFamily:FK,fontSize:"0.72rem",color:"rgba(255,255,255,0.3)",lineHeight:1.5,marginTop:"0.3rem"}}>{j.steps[0]}</p></div>
              </button>
            )}
          </div>))}
      </div>)}
    </div>);}

// ============================================================
// STATS & JOURNAL PAGE
// ============================================================
function StatsPage({stats,journal,favourites}){
  const [tab,setTab]=useState("stats");
  const ts=a=>({background:a?"rgba(255,255,255,0.08)":"none",border:`1px solid ${a?"rgba(255,255,255,0.15)":"rgba(255,255,255,0.05)"}`,borderRadius:100,padding:"0.55rem 1.1rem",fontFamily:FK,fontSize:"0.7rem",color:a?"rgba(255,255,255,0.8)":"rgba(255,255,255,0.3)",cursor:"pointer",transition:"all 0.3s ease",letterSpacing:"0.05em"});

  // Calculate stats
  const totalMins=stats.reduce((a,s)=>a+s.mins,0);
  const totalSessions=stats.length;
  const catCounts={};stats.forEach(s=>{catCounts[s.cat]=(catCounts[s.cat]||0)+1});
  const topCat=Object.entries(catCounts).sort((a,b)=>b[1]-a[1])[0];
  const streak=stats.length>0?Math.min(stats.length,stats.filter((_,i)=>i<30).length):0;
  const last7=stats.filter(s=>{const d=new Date(s.date);const w=new Date();w.setDate(w.getDate()-7);return d>=w});

  return(
    <div style={{minHeight:"100vh",background:"linear-gradient(180deg, #0e100f 0%, #0a0f0c 100%)",padding:"2rem 1.25rem"}}>
      <div style={{textAlign:"center",marginBottom:"1.5rem",animation:"fadeUp 0.6s ease"}}>
        <h1 style={{fontFamily:FS,fontSize:"clamp(1.6rem, 5vw, 2.4rem)",fontWeight:300,color:"rgba(255,255,255,0.88)",margin:"0 0 0.4rem"}}>Your Practice</h1>
        <p style={{fontFamily:FK,fontSize:"0.8rem",color:"rgba(255,255,255,0.3)",maxWidth:380,margin:"0 auto",lineHeight:1.6}}>Stats, journal, and your mindfulness journey.</p></div>
      <div style={{display:"flex",justifyContent:"center",gap:"0.5rem",marginBottom:"2rem"}}>
        <button onClick={()=>setTab("stats")} style={ts(tab==="stats")}>📊 Stats</button>
        <button onClick={()=>setTab("journal")} style={ts(tab==="journal")}>📝 Journal</button>
        <button onClick={()=>setTab("favourites")} style={ts(tab==="favourites")}>♥ Favourites</button>
      </div>

      {tab==="stats"&&(<div style={{maxWidth:600,margin:"0 auto",animation:"fadeUp 0.5s ease"}}>
        <div style={{display:"grid",gridTemplateColumns:"repeat(2, 1fr)",gap:"0.75rem",marginBottom:"1.5rem"}}>
          {[{l:"Total Minutes",v:totalMins,icon:"⏱️"},{l:"Sessions Done",v:totalSessions,icon:"🧘"},{l:"This Week",v:last7.length,icon:"📅"},{l:"Top Category",v:topCat?CATEGORIES.find(c=>c.id===topCat[0])?.name:"—",icon:topCat?CATEGORIES.find(c=>c.id===topCat[0])?.icon:"—"}].map((s,i)=>(
            <div key={i} style={{background:"rgba(255,255,255,0.025)",border:"1px solid rgba(255,255,255,0.06)",borderRadius:16,padding:"1.25rem",textAlign:"center",animation:`fadeUp 0.5s ease ${i*0.08}s both`}}>
              <span style={{fontSize:"1.3rem",display:"block",marginBottom:"0.4rem"}}>{s.icon}</span>
              <span style={{fontFamily:FS,fontSize:"1.4rem",color:"rgba(255,255,255,0.85)",fontWeight:300,display:"block"}}>{s.v}</span>
              <span style={{fontFamily:FK,fontSize:"0.55rem",color:"rgba(255,255,255,0.3)",letterSpacing:"0.15em",textTransform:"uppercase"}}>{s.l}</span></div>))}
        </div>

        {/* Category breakdown */}
        {Object.keys(catCounts).length>0&&(<div style={{background:"rgba(255,255,255,0.025)",border:"1px solid rgba(255,255,255,0.06)",borderRadius:16,padding:"1.25rem",marginBottom:"1.5rem"}}>
          <span style={{fontFamily:FK,fontSize:"0.55rem",color:"rgba(255,255,255,0.3)",letterSpacing:"0.15em",textTransform:"uppercase",display:"block",marginBottom:"1rem"}}>Category Breakdown</span>
          {CATEGORIES.map(cat=>{const count=catCounts[cat.id]||0;const pct=totalSessions?count/totalSessions*100:0;return(
            <div key={cat.id} style={{display:"flex",alignItems:"center",gap:"0.75rem",marginBottom:"0.6rem"}}>
              <span style={{fontSize:"0.9rem",width:24}}>{cat.icon}</span>
              <span style={{fontFamily:FK,fontSize:"0.75rem",color:"rgba(255,255,255,0.5)",width:80}}>{cat.name}</span>
              <div style={{flex:1,height:6,background:"rgba(255,255,255,0.04)",borderRadius:3,overflow:"hidden"}}>
                <div style={{height:"100%",width:`${pct}%`,background:cat.color,borderRadius:3,transition:"width 0.5s ease"}}/></div>
              <span style={{fontFamily:FK,fontSize:"0.6rem",color:"rgba(255,255,255,0.25)",width:28,textAlign:"right"}}>{count}</span></div>)})}
        </div>)}
        {totalSessions===0&&<div style={{textAlign:"center",padding:"2rem"}}><span style={{fontSize:"2rem",display:"block",marginBottom:"1rem"}}>🌱</span><p style={{fontFamily:FS,fontSize:"1.1rem",color:"rgba(255,255,255,0.4)",fontStyle:"italic"}}>Complete your first exercise to start tracking stats.</p></div>}

        {/* Export */}
        {totalSessions>0&&<div style={{textAlign:"center",marginTop:"1rem"}}>
          <button onClick={()=>{const data=JSON.stringify({stats,journal,exportDate:new Date().toISOString()},null,2);const blob=new Blob([data],{type:"application/json"});const url=URL.createObjectURL(blob);const a=document.createElement("a");a.href=url;a.download=`mindful-minutes-export-${dk(new Date())}.json`;a.click();URL.revokeObjectURL(url)}} style={{background:"rgba(255,255,255,0.04)",border:"1px solid rgba(255,255,255,0.1)",borderRadius:100,padding:"0.6rem 1.5rem",fontFamily:FK,fontSize:"0.7rem",color:"rgba(255,255,255,0.35)",cursor:"pointer",letterSpacing:"0.1em"}}>📥 Export Data</button>
        </div>}
      </div>)}

      {tab==="journal"&&(<div style={{maxWidth:600,margin:"0 auto",animation:"fadeUp 0.5s ease"}}>
        {journal.length>0?journal.slice().reverse().map((entry,i)=>(
          <div key={i} style={{background:"rgba(255,255,255,0.025)",border:"1px solid rgba(255,255,255,0.06)",borderRadius:14,padding:"1.1rem 1.25rem",marginBottom:"0.6rem",animation:`fadeUp 0.4s ease ${i*0.05}s both`}}>
            <div style={{display:"flex",justifyContent:"space-between",marginBottom:"0.5rem"}}>
              <span style={{fontFamily:FK,fontSize:"0.6rem",color:"rgba(167,199,168,0.5)",letterSpacing:"0.1em"}}>{entry.exercise||"Free writing"}</span>
              <span style={{fontFamily:FK,fontSize:"0.6rem",color:"rgba(255,255,255,0.2)"}}>{entry.date}</span></div>
            <p style={{fontFamily:FK,fontSize:"0.85rem",color:"rgba(255,255,255,0.6)",lineHeight:1.65,margin:0}}>{entry.text}</p></div>
        )):<div style={{textAlign:"center",padding:"2rem"}}><span style={{fontSize:"2rem",display:"block",marginBottom:"1rem"}}>📝</span>
          <p style={{fontFamily:FS,fontSize:"1.1rem",color:"rgba(255,255,255,0.4)",fontStyle:"italic"}}>Journal entries appear here after exercises.</p></div>}
      </div>)}

      {tab==="favourites"&&(<div style={{maxWidth:600,margin:"0 auto",animation:"fadeUp 0.5s ease"}}>
        {favourites.length>0?EXERCISES.filter(e=>favourites.includes(e.id)).map((ex,idx)=>{const cat=CATEGORIES.find(c=>c.id===ex.cat);return(
          <div key={ex.id} style={{background:"rgba(255,255,255,0.025)",border:"1px solid rgba(255,255,255,0.06)",borderRadius:14,padding:"1rem 1.25rem",marginBottom:"0.6rem",animation:`fadeUp 0.4s ease ${idx*0.05}s both`}}>
            <div style={{display:"flex",justifyContent:"space-between",alignItems:"center"}}>
              <span style={{fontFamily:FK,fontSize:"0.6rem",color:cat.color,letterSpacing:"0.12em",textTransform:"uppercase",opacity:0.7}}>{cat.icon} {cat.name}</span>
              <span style={{fontFamily:FK,fontSize:"0.65rem",color:"rgba(255,255,255,0.25)"}}>{ex.mins} min</span></div>
            <span style={{fontFamily:FS,fontSize:"1.1rem",color:"rgba(255,255,255,0.8)",display:"block",marginTop:"0.3rem"}}>{ex.name}</span></div>)})
        :<div style={{textAlign:"center",padding:"2rem"}}><span style={{fontSize:"2rem",display:"block",marginBottom:"1rem"}}>♥</span>
          <p style={{fontFamily:FS,fontSize:"1.1rem",color:"rgba(255,255,255,0.4)",fontStyle:"italic"}}>Tap the heart on any exercise to save it here.</p></div>}
      </div>)}
    </div>);}

// ============================================================
// NAVIGATION
// ============================================================
function NavBar({activePage,setActivePage,onSettingsClick}){
  const pages=[
    {id:"zen",label:"Zen",icon:<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"><circle cx="12" cy="12" r="10"/><path d="M12 2a14 14 0 0 0 0 20"/><path d="M2 12h20"/></svg>},
    {id:"minutes",label:"Minutes",icon:<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"><circle cx="12" cy="12" r="10"/><path d="M12 6v6l4 2"/></svg>},
    {id:"mood",label:"Mood",icon:<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78L12 21.23l8.84-8.84a5.5 5.5 0 0 0 0-7.78z"/></svg>},
    {id:"escapes",label:"Escapes",icon:<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"><path d="M3 18v-6a9 9 0 0 1 18 0v6"/><path d="M21 19a2 2 0 0 1-2 2h-1a2 2 0 0 1-2-2v-3a2 2 0 0 1 2-2h3zM3 19a2 2 0 0 0 2 2h1a2 2 0 0 0 2-2v-3a2 2 0 0 0-2-2H3z"/></svg>},
    {id:"practice",label:"Practice",icon:<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"><path d="M12 20V10"/><path d="M18 20V4"/><path d="M6 20v-4"/></svg>},
  ];
  return(
    <nav style={{position:"fixed",bottom:0,left:0,right:0,zIndex:100,background:"rgba(10,15,12,0.88)",backdropFilter:"blur(24px)",WebkitBackdropFilter:"blur(24px)",borderTop:"1px solid rgba(255,255,255,0.05)",padding:"0.4rem 0.25rem calc(0.4rem + env(safe-area-inset-bottom))",display:"flex",justifyContent:"space-around",alignItems:"center"}}>
      {pages.map(p=>{const a=activePage===p.id;return(
        <button key={p.id} onClick={()=>setActivePage(p.id)} style={{background:"none",border:"none",display:"flex",flexDirection:"column",alignItems:"center",gap:"3px",padding:"0.4rem 0.5rem",cursor:"pointer",color:a?"rgba(167,199,168,0.95)":"rgba(255,255,255,0.28)",transition:"all 0.3s ease",transform:a?"translateY(-1px)":"none"}}>
          {p.icon}<span style={{fontFamily:FK,fontSize:"0.52rem",letterSpacing:"0.08em",textTransform:"uppercase",fontWeight:a?500:400}}>{p.label}</span>
          {a&&<div style={{width:3,height:3,borderRadius:"50%",background:"rgba(167,199,168,0.6)",marginTop:1}}/>}
        </button>)})}
      <button onClick={onSettingsClick} style={{background:"none",border:"none",display:"flex",flexDirection:"column",alignItems:"center",gap:"3px",padding:"0.4rem 0.5rem",cursor:"pointer",color:"rgba(255,255,255,0.28)"}}>
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg>
        <span style={{fontFamily:FK,fontSize:"0.52rem",letterSpacing:"0.08em",textTransform:"uppercase"}}>Config</span>
      </button>
    </nav>);}

// ============================================================
// MAIN APP
// ============================================================
export default function MindfulMinutes(){
  const [page,setPage]=useState("zen");
  const [unsplashKey,setUnsplashKey]=useState("");
  const [settingsOpen,setSettingsOpen]=useState(false);
  const [refreshKey,setRefreshKey]=useState(0);
  const [intention,setIntention]=useState("");
  const [favourites,setFavourites]=useState([]);
  const [stats,setStats]=useState([]);
  const [journal,setJournal]=useState([]);
  const [notifications,setNotifications]=useState({morning:false,afternoon:false,evening:false});

  const handleSaveKey=(key)=>{setUnsplashKey(key);setRefreshKey(k=>k+1)};
  const toggleFav=(id)=>setFavourites(p=>p.includes(id)?p.filter(x=>x!==id):[...p,id]);
  const addStats=(exercise)=>setStats(p=>[...p,{id:exercise.id,cat:exercise.cat,name:exercise.name,mins:exercise.mins,date:dk(new Date())}]);
  const addJournal=(text,exerciseName)=>setJournal(p=>[...p,{text,exercise:exerciseName||null,date:dk(new Date())}]);

  const renderPage=()=>{switch(page){
    case "zen":return <ZenPage key={refreshKey} unsplashKey={unsplashKey} intention={intention} setIntention={setIntention}/>;
    case "minutes":return <MinutesPage favourites={favourites} toggleFav={toggleFav} stats={stats} addStats={addStats} journal={journal} addJournal={addJournal}/>;
    case "mood":return <MoodBoardPage navigateTo={setPage}/>;
    case "escapes":return <GuidedEscapesPage/>;
    case "practice":return <StatsPage stats={stats} journal={journal} favourites={favourites}/>;
    default:return <ZenPage key={refreshKey} unsplashKey={unsplashKey} intention={intention} setIntention={setIntention}/>;
  }};

  return(<>
    <style>{`
      @import url('https://fonts.googleapis.com/css2?family=Cormorant+Garamond:ital,wght@0,300;0,400;1,300;1,400&family=Karla:wght@300;400;500&display=swap');
      * { margin:0; padding:0; box-sizing:border-box; }
      html, body { background:#0a0f0c; overflow-x:hidden; }
      @keyframes fadeUp { from { opacity:0; transform:translateY(20px); } to { opacity:1; transform:translateY(0); } }
      @keyframes pulse { 0%,100% { opacity:0.6; transform:scale(1); } 50% { opacity:1; transform:scale(1.4); } }
      .breathing-circle { width:56px; height:56px; border-radius:50%; border:1px solid rgba(167,199,168,0.25); animation:breathe 4s ease-in-out infinite; }
      @keyframes breathe { 0%,100% { transform:scale(1); opacity:0.35; } 50% { transform:scale(1.3); opacity:0.75; } }
      ::-webkit-scrollbar { display:none; } html { scrollbar-width:none; }
      input[type=range] { -webkit-appearance:none; background:rgba(255,255,255,0.08); border-radius:4px; outline:none; }
      input[type=range]::-webkit-slider-thumb { -webkit-appearance:none; width:14px; height:14px; border-radius:50%; background:rgba(255,255,255,0.6); cursor:pointer; }
    `}</style>
    <SettingsPanel apiKey={unsplashKey} setApiKey={handleSaveKey} isOpen={settingsOpen} setIsOpen={setSettingsOpen} notifications={notifications} setNotifications={setNotifications}/>
    <div style={{paddingBottom:72}}>{renderPage()}</div>
    <NavBar activePage={page} setActivePage={setPage} onSettingsClick={()=>setSettingsOpen(true)}/>
  </>);}
