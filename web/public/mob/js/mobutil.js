;
var MOB_UTIL = {};
(function(MOB_UTIL) {
    /**
     * get方式请求，尽量少用
     * @param params  {url:"http://www.baidu.com",params:{}}
     * @returns
     */
    MOB_UTIL.doGet = function getData(params) {
        var dfd = $.Deferred();
        mintUI.Indicator.open();
        axios({
            method: 'get',
            params: {
                "data": encodeURI(JSON.stringify(params.params ? params.params : {}))
            },
            url: params.url
        }).then(function(res) {
            mintUI.Indicator.close();
            var result = res.data;
            if (result.code === '0') {
                dfd.resolve(result);
            } else {
                result.msg = result.msg || '系统内部错误，请联系管理员';
                mintUI.MessageBox('提示', result.msg);
                dfd.reject(result);
            }
        }).catch(function(err) {
            //打印堆栈
            console.error(err);
            mintUI.Indicator.close();
            if (err.response) {
                mintUI.MessageBox('网络错误(' + err.response.status + ')', err.message);
            } else {
                err.message = err.message == 'Network Error' ? '网络连接不可用' : err.message;
                mintUI.MessageBox('错误', err.message);
            }
            dfd.reject(err);
        });
        return dfd;
    };
    /**
     * 异步数据请求，主要使用方式
     * @param params
     * @returns
     */
    MOB_UTIL.doPost = function getData(params) {
        var dfd = $.Deferred();
        mintUI.Indicator.open();
        axios({
            method: 'post',
            data: params.params ? params.params : {},
            url: params.url,
            transformRequest: [function(data) {
                return "data=" + encodeURIComponent(JSON.stringify(data));
            }]
        }).then(function(res) {
            mintUI.Indicator.close();
            var result = res.data;
            if (result.code === '0') {
                dfd.resolve(result);
            } else {
                result.msg = result.msg || '系统内部错误，请联系管理员';
                mintUI.MessageBox('提示', result.msg);
                dfd.reject(result);
            }
        }).catch(function(err) {
            //打印堆栈
            console.error(err);
            mintUI.Indicator.close();
            if (err.response) {
                mintUI.MessageBox('网络错误(' + err.response.status + ')', err.message);
            } else {
                err.message = err.message == 'Network Error' ? '网络连接不可用' : err.message;
                mintUI.MessageBox('错误', err.message);
            }
            dfd.reject(err);
        });
        return dfd;
    };

    /**
     * get方式请求，根据actionName查询
     * @param params  
     * @returns
     */
    MOB_UTIL.doActionQuery = function getData(params) {
        var dfd = $.Deferred();
        var actionName = params.name;
        mintUI.Indicator.open();
        axios({
            method: 'get',
            params: params.params ? params.params : {},
            url: params.url
        }).then(function(res) {
            mintUI.Indicator.close();
            var result = res.data;
            if (result.code === '0') {
                dfd.resolve(result.datas[actionName].rows);
            } else {
                result.msg = result.msg || '系统内部错误，请联系管理员';
                mintUI.MessageBox('提示', result.msg);
                dfd.reject(result);
            }
        }).catch(function(err) {
            //打印堆栈
            console.error(err);
            mintUI.Indicator.close();
            if (err.response) {
                mintUI.MessageBox('网络错误(' + err.response.status + ')', err.message);
            } else {
                err.message = err.message == 'Network Error' ? '网络连接不可用' : err.message;
                mintUI.MessageBox('错误', err.message);
            }
            dfd.reject(err);
        });
        return dfd;
    };

    /**
     * @param params  
     * @returns
     */
    MOB_UTIL.doActionExecute = function getData(params) {
        var dfd = $.Deferred();
        var actionName = params.name;
        mintUI.Indicator.open();
        axios({
            method: 'post',
            params: params.params ? params.params : {},
            url: params.url
        }).then(function(res) {
            mintUI.Indicator.close();
            var result = res.data;
            if (result.code === '0') {
                dfd.resolve(result.datas[actionName].rows);
            } else {
                result.msg = result.msg || '系统内部错误，请联系管理员';
                mintUI.MessageBox('提示', result.msg);
                dfd.reject(result);
            }
        }).catch(function(err) {
            //打印堆栈
            console.error(err);
            mintUI.Indicator.close();
            if (err.response) {
                mintUI.MessageBox('网络错误(' + err.response.status + ')', err.message);
            } else {
                err.message = err.message == 'Network Error' ? '网络连接不可用' : err.message;
                mintUI.MessageBox('错误', err.message);
            }
            dfd.reject(err);
        });
        return dfd;
    };
    
    /**
     * 用于提交大文本内容
     * 保存到数据模型中
     * params.params 数据格式如下
     * var paramObj = {}；
     * paramObj.DATA = JSON.stringify(self.formValue);
     * paramObj.DATA_MODEL = "T_IT_XSQK";
     * paramObj.ACTION_TYPE = "ADD";新增/编辑 ADD/SAVE
     * @param params  
     * @returns
     */
    MOB_UTIL.doActionPostExecute = function getData(params) {
        var dfd = $.Deferred();
        mintUI.Indicator.open();
        axios({
            method: 'post',
            data: params.params ? params.params : {},
            url: contextPath+"/sys/itpub/publicApplySetting/dataModelExecute.do",
            transformRequest: [function(data) {
                return "data=" + encodeURIComponent(JSON.stringify(data));
            }]
        }).then(function(res) {
            mintUI.Indicator.close();
            var result = res.data;
            if (result.code === '0') {
                dfd.resolve(result);
            } else {
                result.msg = result.msg || '系统内部错误，请联系管理员';
                mintUI.MessageBox('提示', result.msg);
                dfd.reject(result);
            }
        }).catch(function(err) {
            //打印堆栈
            console.error(err);
            mintUI.Indicator.close();
            if (err.response) {
                mintUI.MessageBox('网络错误(' + err.response.status + ')', err.message);
            } else {
                err.message = err.message == 'Network Error' ? '网络连接不可用' : err.message;
                mintUI.MessageBox('错误', err.message);
            }
            dfd.reject(err);
        });
        return dfd;
    };
})(window.MOB_UTIL = MOB_UTIL);