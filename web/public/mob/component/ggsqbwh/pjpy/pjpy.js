define(function(require, exports, module) {
    var tpl = require('text!publicVueComponent/ggsqbwh/pjpy/pjpyTpl.html');
    var publicUtils = require('publicVueComponent/ggsqbwh/publicUtils');
    
    return function() {
        var page = {
            template: tpl,
            data: function() {
                return {
                	//选中值
                	selectVal:[],
                	//默认最大选择数10条
                	maxSelNum:10,
                	//数据来源
                	sjly:[],
                	//数据范围
                	sjfw:{},
                	//是否控制条数
                	szts:{},
                	//控制条数数据
                	szs:{},
                	//条数
                	ts:0,
                	//当前学年信息
                	dqxn:"",
                	//获奖记录结果数据
                	pjpyArray:[],
                	//是否新增
                	isAdd:false,
                	formValue:{},
                	model: [],
                	customVm: {},
                    readonly: false,
                    //是否存在数据
                    hasDatas:true,
                    //确认按钮是否显示
                    confirmShow:true
                };
            },
            //编辑和新增 editFLag = true 编辑情况下wid不为空
            //查看详情editFlag=false 只展示已经选择的评奖记录
            props: ['xsbh', 'editFlag', 'zldm','wid','mkdm','xzxn','selectPjpyVal'],
            
            created: function() {
            	 var self = this;
            	 self.pjpyArray = [];
            	 if(self.selectPjpyVal){
            		 self.selectVal = self.selectPjpyVal.split(",");
            	 }
            	 self.getFzSettingInfo().then(self.getFZModel).then(self.loadSqFormFun);
            },
            methods: {
            	 //表单模型
                getFZModel: function() {
                    var dfd = $.Deferred();
                    //获取表单模型
                    var self = this;
                    self.model = WIS_EMAP_SERV.getModel(SWROOT_PATH + "/sqszpubjs/apply.do", "yddxrxshjjlbd", "form");	
                    for (var index in self.model) {
                        if (self.model[index].name == "JLXN") {
                            var urlParam = '';
                            if (self.sjfw && self.sjfw.ZDZ && self.sjfw.ZDZ == '1') {
                                urlParam = ' IS_PRE=true ';
                            }
                            self.model[index].url = publicUtils.getDictionaryOfFbxn() + "?data={" + urlParam + "}";
                        }
                    }
                    dfd.resolve();
                    return dfd;
                },
            	
            	//获取模块信息
            	getmkInfo : function(){
            		var self = this;
            		actionName = "";
            		if(self.mkdm == 'JXJ'){
            			actionName = 'T_JXJ_PDXX';
            		}
            		if(self.mkdm == 'RYCH'){
            			actionName = 'T_RYCH_GRPDXX';
            		}
            		if(self.mkdm == 'ZXJ'){
            			actionName = 'T_ZXJ_PDXX';
            		}
            		return actionName;
            	},
            	
            	//是否存在数据
            	hasData:function(){
            		var self = this;
            		if(self.pjpyArray.length > 0){
            			self.hasDatas = true;
            		}else{
            			self.hasDatas = false;
            		}
            	},
            	//加载申请页面评奖评优数据信息
            	loadSqFormFun:function(){
            		var self = this;
            		self.pjpyArray = [];
            		//已选在的评奖评优记录
            		var selValStr = self.selectVal.join(",");
            		//根据分组设置查询获奖记录信息
            		for(var i=0;i < self.sjly.length;i++){
        				//查询数据
        				var param = {};
        				var querySetting = [];
        				querySetting.push({
        					"caption":"",
        					"name":"XSBH",
        					"value":self.xsbh,
        					"builder":"equal",
        					"linkOpt":"AND"
        				});
        				if(self.sjfw && self.sjfw.ZDZ && self.sjfw.ZDZ == '1' && self.xzxn){
        					if(self.sjly[i] == '3'){
        						querySetting.push({
        							"caption":"",
        							"name":"JLXN",
        							"value":parseInt(self.xzxn)-1 ,
        							"builder":"equal",
        							"linkOpt":"AND"
        						});
        					}
        					else{
        						querySetting.push({
        							"caption":"",
        							"name":"PDXN",
        							"value":parseInt(self.xzxn)-1 ,
        							"builder":"equal",
        							"linkOpt":"AND"
        						});
        					}
        				}
        				param['querySetting'] = JSON.stringify(querySetting);
        				// 查询   ----->奖学金
        				if(self.sjly[i] == '1'){
        					MOB_UTIL.doActionQuery({
    	                        url: SWROOT_PATH + "/sqszpubjs/apply/cxxsjxjlsjl.do",
    	                        params: param,
    	                        name: "cxxsjxjlsjl"
    	                    }).done(function(result) {
    	                    	if (result && result.length > 0) {
    	                    		for(var j = 0 ; j <  result.length ; j++){
    	                    			var infoTmp = (result[j].PDXN_DISPLAY ? result[j].PDXN_DISPLAY : '') + "  " +
		    							(result[j].JXJMC ? result[j].JXJMC : '')    + "  " + 
		    							(result[j].DJMC ? result[j].DJMC : '')    + "  " + 
		    							(result[j].SLDW ? result[j].SLDW : '') + "  " +
		    							(result[j].JBMC ? result[j].JBMC : '');
    	                    			
    	                    			var tWid = result[j].WID;
    	                    			//只读模式下 且已经选择的评奖评优包含该条评奖评优
    	                    			if(!self.editFlag){
    	                    				var tmpObj = {};
    	                    				if(selValStr.indexOf(tWid) >= 0){
    	                    					tmpObj['disabled']=true;
    	                    					tmpObj['WID'] = tWid;
    	                    					tmpObj['HJXSZ'] = infoTmp;
    	                    					self.pjpyArray.push(tmpObj);
    	                    				}
    	                    			}else{
    	                    				//编辑模式下 
    	                    				var tmpObj = {};
	                    					tmpObj['disabled']=false;
	                    					tmpObj['WID'] = tWid;
	                    					tmpObj['HJXSZ'] = infoTmp;
	                    					self.pjpyArray.push(tmpObj);
    	                    			}
    	                    		}
    	                    		if(self.pjpyArray.length > 0){
		                    			self.hasDatas = true;
		                    		}else{
		                    			self.hasDatas = false;
		                    		}
    	                    	}
    	                    });
        				}
        				//荣誉称号
        				if(self.sjly[i] == '2'){
        					MOB_UTIL.doActionQuery({
    	                        url: SWROOT_PATH + "/sqszpubjs/apply/cxxsrychlsjl.do",
    	                        params: param,
    	                        name: "cxxsrychlsjl"
    	                    }).done(function(result) {
    	                    	if (result && result.length > 0) {
    	                    		for(var j = 0 ; j <  result.length ; j++){
    	                    			var infoTmp = (result[j].PDXN_DISPLAY ? result[j].PDXN_DISPLAY : '') + "  " +
		    							(result[j].RYCHMC ? result[j].RYCHMC : '')    + "  " + 
		    							(result[j].SLDW ? result[j].SLDW : '') + "  " +
		    							(result[j].JLJB_DISPLAY ? result[j].JLJB_DISPLAY : '');
    	                    			
    	                    			var tWid = result[j].WID;
    	                    			//只读模式下 且已经选择的评奖评优包含该条评奖评优
    	                    			if(!self.editFlag){
    	                    				if(selValStr.indexOf(tWid) >= 0){
    	                    					var tmpObj = {};
    	                    					tmpObj['disabled']=true;
    	                    					tmpObj['WID'] = tWid;
    	                    					tmpObj['HJXSZ'] = infoTmp;
    	                    					self.pjpyArray.push(tmpObj);
    	                    				}
    	                    			}else{
    	                    				//编辑模式下 
    	                    				var tmpObj = {};
	                    					tmpObj['disabled']=false;
	                    					tmpObj['WID'] = tWid;
	                    					tmpObj['HJXSZ'] = infoTmp;
	                    					self.pjpyArray.push(tmpObj);
    	                    			}
    	                    		}
    	                    		if(self.pjpyArray.length > 0){
		                    			self.hasDatas = true;
		                    		}else{
		                    			self.hasDatas = false;
		                    		}
    	                    	}
    	                    });
        				}
        				//其他获奖信息
        				if(self.sjly[i] == '3'){
        					MOB_UTIL.doActionQuery({
    	                        url: SWROOT_PATH + "/sqszpubjs/apply/cxygxsdhjxx.do",
    	                        params: param,
    	                        name: "cxygxsdhjxx"
    	                    }).done(function(result) {
    	                    	if (result && result.length > 0) {
		                    		for(var j = 0 ; j <  result.length ; j++){
		                    			var infoTmp = (result[j].JLXN_DISPLAY ? result[j].JLXN_DISPLAY : '') + "  " +
		    							(result[j].JLMC ? result[j].JLMC : '')    + "  " + 
		    							(result[j].JLBM ? result[j].JLBM : '') + "  " +
		    							(result[j].JLJB_DISPLAY ? result[j].JLJB_DISPLAY : '');
		                    			
		                    			var tWid = result[j].WID;
    	                    			//只读模式下 且已经选择的评奖评优包含该条评奖评优
    	                    			if(!self.editFlag){
    	                    				if(selValStr.indexOf(tWid) >= 0){
    	                    					var tmpObj = {};
    	                    					tmpObj['disabled']=true;
    	                    					tmpObj['WID'] = tWid;
    	                    					tmpObj['HJXSZ'] = infoTmp;
    	                    					self.pjpyArray.push(tmpObj);
    	                    				}
    	                    			}else{
    	                    				var tmpObj = {};
    	                    				//编辑模式下 
	                    					tmpObj['disabled']=false;
	                    					tmpObj['WID'] = tWid;
	                    					tmpObj['HJXSZ'] = infoTmp;
	                    					self.pjpyArray.push(tmpObj);
    	                    			}
		                    		}
		                    		if(self.pjpyArray.length > 0){
		                    			self.hasDatas = true;
		                    		}else{
		                    			self.hasDatas = false;
		                    		}
    	                    	}
    	                    });
        				}
            		}
            	},
            	
            	//获取分组设置信息
            	getFzSettingInfo:function(){
            		var dfd = $.Deferred();
            		var self = this;
            		var param = {};
	        		param['FZDM'] = "PJPYFZ";
	        		param['ZLDM'] = self.zldm;
	        		param['SFSY'] = "1";
	                MOB_UTIL.doActionQuery({
	                    url: SWROOT_PATH + "/sqszpubjs/apply/T_XG_FZZD_SZ_QUERY.do",
	                    params: param,
	                    name: "T_XG_FZZD_SZ_QUERY"
	                }).done(function(result) {
	                    if (result && result.length > 0) {
	                    	for(var i = 0 ; i < result.length ; i++){
	            				//数据范围
	            				if(result[i].ZDDM && result[i].ZDDM == 'SJFW'){
	            					self.sjfw = result[i];
	            				}
	            				//是否进行条数空值
	            				if(result[i].ZDDM && result[i].ZDDM == 'TSKZ'){
	            					self.szts = result[i];
	            				}
	            				if(result[i].ZDDM && result[i].ZDDM == 'TS'){
	            					self.szs = result[i];
	            				}
	            				//数据来源
	            				if(result[i].ZDDM && result[i].ZDDM == 'SJLY' && result[i].ZDZ){
	            					self.sjly = result[i].ZDZ.split(',');
	            				}
	            			}
	                    	//获得设置的条数信息
	                		if(self.szts && self.szts.ZDZ && self.szts.ZDZ == '1' && self.szs && self.szs.ZDZ){
	                			self.ts = self.szs.ZDZ;
	                			self.maxSelNum = self.ts;
	                		}
	                    }
	                    dfd.resolve();
	                });
	                return dfd;
            	},
            	//新增获奖记录
            	addhjjl:function(){
            		//新增状态再点之间返回
                    if (this.isAdd) {
                        return;
                    }
                    this.isAdd = true;
                    this.confirmShow = false;
                    this.formValue = {};
            	},
            	
            	//取消新增
            	cancelAdd :function(){
            		this.isAdd = false;
            		this.confirmShow = true;
            	},
            	//新增获奖记录信息
            	saveHjjl :function(){
            		var self = this;
            		if(!this.$refs.form.validate()){
            			return false;
            		}
            		var param = self.formValue;
            		param.XSBH = self.xsbh;
                    //修改保存
                    MOB_UTIL.doActionExecute({
                        url: SWROOT_PATH + "/sqszpubjs/apply/T_XG_XS_JLXX_ADD.do",
                        params: param,
                        name: "T_XG_XS_JLXX_ADD"
                    }).done(function(result) {
                        mintUI.Toast({
                            message: '保存成功',
                            iconClass: 'iconfont mint-icon-i icon-chenggong'
                        });
                        self.loadSqFormFun();
                    });
            		this.isAdd = false;
            		this.confirmShow = true;
            	},
                //确认返回上一级
                confirm:function(){
                	var self = this;
                	//获取选择的获奖记录
                	var selValue = self.selectVal.join(",");
                	var retParam = {};
                    retParam.FZDM = 'PJPYFZ';
                    retParam.PJPY_VALUE = selValue;
                    self.$emit('save', retParam);
                    self.$router.go(-1);
                }
            }
        };
        return page;
    };

});