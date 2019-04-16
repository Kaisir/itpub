define(function(require, exports, module) {
    var tpl = require('text!publicVueComponent/selectrole/selectrole.html');
    var spriteUtils = require('spriteUtils');

    return function() {
        var page = {
            template: tpl,
            data: function() {
                return {
                    //用于控制是否展示空页面
                    hasDatas: true,
                    //当前展示的数组
                    list: [],
                    styleObject: {
                        "margin-top": ""
                    }
                };
            },
            created: function() {
                SDK.setTitleText("选择角色");
                this.qryUserRoles();
            },
            updated: function() {
                //计算页面高度，使加载的角色内容始终居中
                var screenHeight = document.documentElement.clientHeight;
                var roleNum = this.list.length;
                if (screenHeight <= 109 * roleNum) {
                    this.styleObject["margin-top"] = "20px !important";
                } else {
                    var diff = screenHeight - 109 * roleNum;
                    this.styleObject["margin-top"] = diff / 2 + "px !important";
                }
            },
            methods: {
                /**
                 * 查询用户角色
                 */
                qryUserRoles: function() {
                    var params = {
                        url: WIS_CONFIG.ROOT_PATH + '/sys/itpub/MobileCommon/getUserRoles.do',
                        params: {
                            APPID: WIS_CONFIG.APPID
                        }
                    };
                    var self = this;
                    MOB_UTIL.doPost(params).done(function(result) {
                        var roleListRtn = result.data.ROLELIST;
                        if (roleListRtn && roleListRtn.length > 0) {
                            self.list = roleListRtn;
                            self.hasDatas = true;
                        } else {
                            self.list = [];
                            self.hasDatas = false;
                        }
                    });
                },
                /**
                 * 点击角色进入角色配置页面
                 */
                gotoTargetMenu: function(e) {
                    var selRoleId = e.currentTarget.getAttribute("roleId");
                    roleId = selRoleId;
                    var self = this;
                    self.setAppRole(selRoleId).done(function() {
                        spriteUtils.getAuthConfig(true).done(function() {
                            spriteUtils.initApp(function() {
                                //应用初始化完成后删除掉选择角色的dom
                                $('#selectrole').remove();
                            });
                        });
                    });
                },
                /**
                 * 设置登录角色
                 */
                setAppRole: function(roleId) {
                    var params = {
                        url: WIS_CONFIG.ROOT_PATH + '/sys/itpub/MobileCommon/setAppRole.do',
                        params: {
                            APPID: WIS_CONFIG.APPID,
                            APPNAME: WIS_CONFIG.APPNAME,
                            ROLEID: roleId
                        }
                    };
                    return MOB_UTIL.doPost(params);
                }
            }
        };
        return page;
    };

});