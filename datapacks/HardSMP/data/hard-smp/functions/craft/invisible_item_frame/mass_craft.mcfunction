execute as @s run kill @e[type=item,nbt={Item:{id:"minecraft:glow_item_frame",Count:1b},Age:1},distance=..3]

clear @s minecraft:glow_item_frame 1
give @s item_frame{Enchantments:[{}],EntityTag:{Invisible:1b},display:{Name:'{"text":"Invisible Item Frame"}'}} 1
scoreboard players remove @s invisible_item_frame 1

execute if entity @s[scores={invisible_item_frame=1..}] run function hard-smp:craft/invisible_item_frame/mass_craft
