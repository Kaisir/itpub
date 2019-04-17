define(function(require, exports, module) {
    var tpl = require('text!publicVueComponent/ggsqbwh/zlqk/zlqkTpl.html');
    var publicUtils = require('publicVueComponent/ggsqbwh/publicUtils');

    return function() {
        var page = {
            template: tpl,
            data: function() {
                return {
                    //专利情况是否可维护
                    zlqksfkwh: false,
                    //专利情况记录
                    zlqkArray: [],
                    //是否有专利情况记录
                    hasDatas: true,
                    //数据范围
                    sjfw: null,
                    //表单模型
                    model: {},
                    formValue: {},
                    customVm: {},
                    readonly: false,
                    //是否处于新增模式
                    isAdd: false,
                    //是否处于编辑模式
                    isEdit: false,
                    dataReady: false,
                    //用于保存编辑状态时，原表单数据
                    oldEditFormValue: {}
                };
            },
            props: ['xsbh', 'editFlag', 'zldm', 'xzxn'],
            created: function() {
                var self = this;
                //页面初始化
                self.getZlqkSjfw().then(self.getFZModelSFKWH).then(self.getFZModel).then(self.getZlqkRecords);
            },
            methods: {
                //查询数据范围
                getZlqkSjfw: function() {
                    var dfd = $.Deferred();
                    var self = this;
                    var tjParam = {};
                    tjParam['SFSY'] = '1';
                    tjParam['FZDM'] = 'ZLQKFZ';
                    tjParam['ZLDM'] = self.zldm;
                    MOB_UTIL.doActionQuery({
                        url: SWROOT_PATH + "/sqszpubjs/apply/T_XG_FZZD_SZ_QUERY.do",
                        params: tjParam,
                        name: "T_XG_FZZD_SZ_QUERY"
                    }).done(function(result) {
                        if (result && result.length > 0) {
                            for (var i = 0; i < result.length; i++) {
                                // 数据来源
                                if (result[i].ZDDM && result[i].ZDDM == 'SJFW') {
                                    self.sjfw = result[i];
                                }
                            }
                        }
                        dfd.resolve();
                    });
                    return dfd;
                },
                //判断专利情况是否可维护
                getFZModelSFKWH: function() {
                    var dfd = $.Deferred();
                    var self = this;
                    publicUtils.getFZModelSFKWH('ZLQKFZ', self.zldm).done(function(flag) {
                        self.zlqksfkwh = flag;
                        dfd.resolve();
                    });
                    return dfd;
                },
                //表单模型
                getFZModel: function() {
                    var dfd = $.Deferred();
                    //获取表单模型
                    var self = this;
                    publicUtils.getFZModel('cxxszlxxbd', 'ZLQKFZ', self.zldm).done(function(result) {
                        self.model = result;
                        for (var index in self.model) {
                            if (self.model[index].name == "HDXN") {
                                var urlParam = '';
                                if (self.sjfw && self.sjfw.ZDZ && self.sjfw.ZDZ == '1') {
                                    urlParam = ' IS_PRE=true ';
                                }
                                self.model[index].url = publicUtils.getDictionaryOfFbxn() + "?data={" + urlParam + "}";
                            }
                        }
                        dfd.resolve();
                    });
                    return dfd;
                },
                //获取专利情况记录
                getZlqkRecords: function() {
                    var self = this;
                    var dfd = $.Deferred();
                    //参数设置
                    var querySetting = [];
                    querySetting.push({
                        "caption": "",
                        "name": "XSBH",
                        "value": self.xsbh,
                        "builder": "equal",
                        "linkOpt": "AND"
                    });
                    if (self.sjfw && self.sjfw.ZDZ && self.sjfw.ZDZ == '1' && self.xzxn) {
                        querySetting.push({
                            "caption": "",
                            "name": "HDXN",
                            "value": parseInt(self.xzxn) - 1,
                            "builder": "equal",
                            "linkOpt": "AND"
                        });
                    }
                    var param = {};
                    param['querySetting'] = JSON.stringify(querySetting);
                    MOB_UTIL.doActionQuery({
                        url: SWROOT_PATH + "/sqszpubjs/apply/cxygxszlxx.do",
                        params: param,
                        name: "cxygxszlxx"
                    }).done(function(result) {
                        if (result && result.length > 0) {
                            for (var j = 0; j < result.length; j++) {
                                result[j].formName = "form" + j;
                                result[j].formRead = true;
                            }
                            self.zlqkArray = result;
                            self.formatFormModel();
                            self.hasDatas = true;
                        } else {
                            self.hasDatas = false;
                        }
                        self.dataReady = true;
                        dfd.resolve();
                    });
                    return dfd;
                },
                //删除专利情况
                deleteZlqk: function(zlqk) {
                    var self = this;
                    var wid = zlqk.WID;
                    var param = {};
                    param['WID'] = wid;
                    mintUI.MessageBox.confirm('确定要删除该记录吗?', '提示').then(function() {
                        //删除
                        MOB_UTIL.doActionExecute({
                            url: SWROOT_PATH + "/sqszpubjs/apply/T_XG_XS_ZL_DELETE.do",
                            params: param,
                            name: "T_XG_XS_ZL_DELETE"
                        }).done(function(result) {
                            self.getZlqkRecords();
                            mintUI.Toast({
                                message: '删除成功',
                                iconClass: 'iconfont mint-icon-i icon-chenggong'
                            });
                        });
                    }, function() {});
                },
                //编辑专利情况
                editZlqk: function(index) {
                    //编辑状态再点之间返回
                    if (this.isEdit || this.isAdd) {
                        return;
                    }
                    this.isEdit = true;
                    this.oldEditFormValue = this.copyObj(this.zlqkArray[index]);
                    this.zlqkArray[index].formRead = false;
                    this.formatFormModel();
                },
                //取消编辑
                cancelEdit: function(index) {
                    this.isEdit = false;
                    this.zlqkArray[index].formRead = true;
                    this.zlqkArray[index] = this.oldEditFormValue;
                    this.formatFormModel();
                },
                //保存编辑
                saveEdit: function(index) {
                    var currentForm = 'form' + index;
                    if (!this.$refs[currentForm][0].validate()) {
                        return false;
                    }
                    var currentFormValue = this.zlqkArray[index];
                    //修改保存
                    var self = this;
                    MOB_UTIL.doActionExecute({
                        url: SWROOT_PATH + "/sqszpubjs/apply/T_XG_XS_ZL_SAVE.do",
                        params: currentFormValue,
                        name: "T_XG_XS_ZL_SAVE"
                    }).done(function(result) {
                        mintUI.Toast({
                            message: '保存成功',
                            iconClass: 'iconfont mint-icon-i icon-chenggong'
                        });
                        self.isEdit = false;
                        self.isAdd = false;
                        self.getZlqkRecords();
                        self.formatFormModel();
                    });
                },
                //新增论文情况
                addZlqk: function() {
                    //新增状态再点之间返回
                    if (this.isAdd) {
                        return;
                    }
                    this.formatFormModel();
                    this.formValue = {};
                    this.isAdd = true;
                },
                //取消新增
                cancelAdd: function() {
                    this.isAdd = false;
                    this.formValue = {};
                    this.formatFormModel();
                },
                //保存新增
                saveAdd: function() {
                    if (!this.$refs.formAdd.validate()) {
                        return false;
                    }
                    this.formValue.XSBH = this.xsbh;
                    var self = this;
                    //新增
                    MOB_UTIL.doActionExecute({
                        url: SWROOT_PATH + "/sqszpubjs/apply/T_XG_XS_ZL_ADD.do",
                        params: self.formValue,
                        name: "T_XG_XS_ZL_ADD"
                    }).done(function(result) {
                        mintUI.Toast({
                            message: '保存成功',
                            iconClass: 'iconfont mint-icon-i icon-chenggong'
                        });
                        self.getZlqkRecords();
                        self.formatFormModel();
                        self.isEdit = false;
                        self.isAdd = false;
                        //清空
                        self.formValue = {};
                    });
                },
                //格式化表单模型,切换附件字段的展现形式
                formatFormModel: function() {
                    var self = this;
                    for (var index in self.model) {
                        if (self.model[index].name == "FJ") {
                            // if (self.model[index].xtype == 'uploadsingleimage') {
                            //     self.model[index].xtype = 'cache-upload';
                            // } else {
                            self.model[index].xtype = 'uploadsingleimage';
                            // }
                        }
                    }
                },
                //深拷贝
                copyObj: function(obj) {
                    var res = {}
                    for (var key in obj) {
                        res[key] = obj[key]
                    }
                    return res;
                },
                //确认
                confirm: function() {
                    var retParam = {};
                    retParam.FZDM = 'ZLQKFZ';
                    this.$emit('save', retParam);
                    this.$router.go(-1);
                }
            }
        };
        return page;
    };

});