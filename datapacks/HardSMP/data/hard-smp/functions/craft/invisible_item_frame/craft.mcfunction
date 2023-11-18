execute as @a[tag=invisible_item_frame] at @s unless entity @e[type=item,nbt={Item:{id:"minecraft:glow_item_frame",Count:1b},Age:1s},distance=..3] run clear @s minecraft:glow_item_frame 1
execute at @a[tag=invisible_item_frame] run kill @e[type=item,nbt={Item:{id:"minecraft:glow_item_frame",Count:1b},Age:1s},distance=..3]

give @a[tag=invisible_item_frame] item_frame{EntityTag:{Invisible:1b},display:{Name:'{"text":"Invisible Item Frame"}'}} 1
scoreboard players remove @a[tag=invisible_item_frame] invisible_item_frame 1

execute as @a[tag=invisible_item_frame,scores={invisible_item_frame=1..}] run function hard-smp:craft/invisible_item_frame/mass_craft

tag @a[tag=invisible_item_frame] remove invisible_item_frame
