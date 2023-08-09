# Better Combat
[English README](./README.md)
## 特性
1. 改变攻击距离和触及距离。(创造模式: +0.5)
    1. 空手: 3.5
    2. 手持非工具物品: 4.0
    3. 手持剑: 5.5
    4. 手持斧: 5.0
    5. 手持其它工具物品: 4.5
2. 基本血量改变： ` 20 - 4 * difficulty.ordinal() + 2 * floor(player.experienceLevel / 5) `
3. 攻击伤害改变. 你可以等到两倍冷却时间来造成更高的伤害。本模组将攻击伤害由线性公式改为非线性公式来惩罚不满蓄力的伤害。
4. 盾一次可以使用80刻，且现在有了冷却时间。 `cooldown = usingTicks / 4 + 20`
5. 将剑的攻击速度从1.6增加至2.0。
6. 现在如果你的血量低于 `maxHealth * 0.3`，你可以持续获得100刻的敏捷1和急迫2。
7. 现在如果你的饥饿值低于6, 你会持续获得100刻的缓慢1和挖掘疲劳2。

## 要求
| 软件            | 版本          |
|:--------------|:------------|
| Java          | \>=17       |
| Fabric Loader | \>= 0.14.22 |

## 依赖
| 模组名称       | 版本 |
|:-----------|:---|
| Fabric API | *  |

## 环境
同时在客户端和服务端。