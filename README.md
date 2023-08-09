# Better Combat
[中文README](./README.zh_cn.md)
## Features
1. Reach distance and attack distance changed. (Creative mode: +0.5)
   1. Empty hand: 3.5
   2. Hand with non-tool item: 4.0
   3. Hand with sword item: 5.5
   4. Hand with axe item: 5.0
   5. Hand with other tool item: 4.5
2. Basic max health changed: ` 20 - 4 * difficulty.ordinal() + 2 * floor(player.experienceLevel / 5) `
3. Attack damage changed. You can wait double of the cooldown time to reach higher damage. This mod change the calculation formula from linear formula to nonlinear formula to strengthen punishment for not accumulating.
4. The shield can be once hold 80 ticks, and now it has to cool down. `cooldown = usingTicks / 4 + 20`
5. Change swords' attack speed from 1.6 to 2.0.
6. Now, if your health is lower than `maxHealth * 0.3`, you can continuously get SPEED 1 status effect for 100 ticks and HASTE 2 status effect for 100 ticks.
7. Now, if your food level is lower than 6, you can continuously get SLOWNESS 1 status effect for 100 ticks and MINING_FATIGUE 2 status effect for 100 ticks.

## Requirements
| Software      | Version     |
|:--------------|:------------|
| Java          | \>=17       |
| Fabric Loader | \>= 0.14.22 |

## Dependence
| Mod Name   | Version |
|:-----------|:--------|
| Fabric API | *       |

## Environment
Both client and server.