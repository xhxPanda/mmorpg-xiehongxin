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
发送命令使用String，不同的参数使用空格隔开，在发送命令时，第0位是cmd，后面的是所需要的参数

登录或注册（user）
	登录cmd：doLogin 用户id 密码 
	注册cmd：doRegister 用户名称 密码 

场景（scene）
	 进入场景：joinScene 场景Id
	 获取场景中的信息：getSceneUser
	 攻击怪物：attackMonster 技能Id 怪物id
	 攻击角色：attackRole 技能Id 怪物id
	 获取场景中的怪物掉落信息：getRoleKillMonsterBonusInfo
	 拾取场景中的怪物掉落：getRoleKillMonsterBonus 掉落id
	 
角色（role）
	 获取所有的角色： getAllRole
	 创建角色： createRole 职业id 角色名称
	 使用角色：useRole roleId
	 获取用户当前角色信息： getUserNowRole
	 
邮件（email） 
	获取所有邮件： getEmailInfo
	发送邮件：sendEmail 
	获取单个邮件信息：READ_EMAIL = "readEmail
	获取邮件奖励：GET_EMAIL_BONUS = "getEmailBonus

物品（material）
	  展示商店：showGoods
	  买东西：buyGoods" 商品Id 数量
	  卖东西：sellGoods 物品栏id 数量
	  展示背包：showAllMaterial
	  使用物品：useMaterial 物品栏id 数量
	  整理背包：ARRANGE_BAG = "arrangeBag
	  排序背包：SORT_BAG = "sortBag"

信息（message）
	发送世界信息：sendWorldMessage 内容
	发送私聊信息：sendToUser 角色id，内容
	  
装备（equiment）
	 展示装备栏：showEquiment
	 卸下装备：takeOffEquiment 装备类型id
	 
公会(guild) 
	展示公会信息：showGuildInfo
	展示公会仓库：showGuildBank 
	展示公会会员：showGuildMember 
	展示公会申请：showGuildApply 

	创建公会 ：creatGuild 公会名称 公会描述
	发送公会申请：sendGuildApply 公会id 内容
	审查申请：eaminationApply 申请角色id 是否同意（true/false）
	公会捐献物品：donateMaterial 背包index 数量
	公会捐献财富：donateTreasure 财富id 数量
	踢出公会：tickOutMember 角色id
	退出公会：leaveGuild 
	转让公会：transferGuild 角色id
	提取物品：extractMaterial 仓库index 数量
	提取财富：extractTreasure 财富id 数量

队伍（team）
	GET_TEAM_INFO = "getTeamInfo 
	邀请人进入队伍：inviteRoleOrganizeTeam 角色id
	处理邀请队伍的请求：dealTeamApply 邀请的角色id 是否同意（true/false）
	退出队伍：quitTeam 
	转让队长：transferCaptain 角色id
	踢人：tickTeamMate 角色id
	
交易（transcation）
	发起交易请求：requestTransaction 角色id
	处理交易请求：dealTransactionRequest 邀请人id
	放置背包交易物：setMaterial 背包index 数量
	放置财富交易物：setTreasure 财富id 数量
	确认交易：checkConfirm 
	取消交易：stopTransaction 
	
任务（mission）
	接受任务：acceptMission 任务id
	完成任务：missionCompete 任务id
	放弃任务：giveUpMission 任务id
	显示可接任务：showMissionCanAccept 
	显示已接任务：showMissionAccept 

pk(pk)
	邀请对方pk：inviteRolePK 角色id
	处理pk的请求：dealPKApplication 邀请人id

## 后记
希望本项目能够给到希望进入游戏行业的后端开发者或者是经常在学校写crud的朋友们有一个参考。
