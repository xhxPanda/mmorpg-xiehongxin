## 背景
本人从小很少玩游戏，这一个月为了熟悉和抽象mmorpg游戏的业务结构，我在最穷的时候冲了150块魔兽月卡玩起了魔兽世界。

本项目是本人根据魔兽世界的业务所模仿构建的mmorpg游戏服务端练手项目，使用netty作为底层通讯框架。

1、使用java的反射和泛型构建出类似Spring的AOP框架。

2、封装了一套原生JDBC的持久层框架（做项目的感觉好像要比使用持久层框架要灵活一点）

3、大量使用后端内存缓存的方式，主要是使用map存储在内存，少量数据使用了google的缓存系统存储，减少读写数据库带来的性能损害

4、封装了一套事件触发机制（同步的，因为异步做起来有点麻烦，为了先做出效果，后面也许会修改）

## 结构
1、前端入口是index.xml。

## 命令（持续更新）
发送命令使用String，不同的参数使用空格隔开，在发送命令时，第0位是cmd，第1位是userId（之前的设计缺陷，这里真改不动），后面的是所需要的参数

登录或注册（user）:1
	LOGIN = "doLogin";
	REGISTER = "doRegister";
	
场景（scene）：2
	 进入场景：JOIN_SCENE = "joinScene"; （场景id）
	 获取场景中的信息：GET_SCENE_USER = "getSceneUser";
	 攻击怪物：ATTACK_MONSTER = "attackMonster"; （技能Id 怪物id）
	 攻击角色：ATTACK_ROLE = "attackRole"; （技能Id， 角色userId）
	 获取场景中的怪物掉落：GET_ROLE_KILL_MONSTER_BONUS_INFO = "getRoleKillMonsterBonusInfo"; 
	 拾取场景中的怪物掉落：GET_ROLE_KILL_MONSTER_BONUS = "getRoleKillMonsterBonus";（掉落id）
	 
角色（role）：3
	 获取所有的角色： GET_ALL_ROLE = "getAllRole";
	 创建角色： CREATE_ROLE = "createRole";
	 使用角色： USE_ROLE = "useRole";
	 获取用户当前角色信息： GET_USER_NOW_ROLE = "getUserNowRole";
	 
邮件（email）：4	 
	获取所有邮件：GET_EMAIL_INFO = "getEmailInfo";
	发送邮件：SEND_EMAIL = "sendEmail"; 
	获取单个邮件信息：READ_EMAIL = "readEmail";
	获取邮件奖励：GET_EMAIL_BONUS = "getEmailBonus";

物品（material）：5
	  BUY_GOODS = "buyGoods";
	  SELL_GOODS = "sellGoods";
	  SHOW_ALL_MATERIAL = "showAllMaterial";

信息（message）：	  
	发送世界信息：SEND_WORLD_MESSAGE = "sendWorldMessage";
	发送私聊信息：SEND_TO_USER = "sendToUser";
	  
装备（equiment）：7
	 展示装备栏：SHOW_EQUIMENT = "showEquiment";
	 穿上装备：WEAR_EQUIMENT = "wearEquoment";
	 卸下装备：TAKE_OFF_EQUIMENT = "takeOffEquiment";

## 后记
希望本项目能够给到希望进入游戏行业的后端开发者或者是经常在学校写crud的朋友们有一个参考。
