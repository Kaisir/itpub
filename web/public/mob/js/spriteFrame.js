(function(require) {
    //require路径配置
    var requireConfig = {
        paths: {
        	jquery: SERVER_PATH + '/bower_components/jquery/dist/jquery.min',
            vue: SERVER_PATH + '/bower_components/vue2/vue.min',
            vueRouter: SERVER_PATH + '/bower_components/vue2/vue-router.min',
            text: SERVER_PATH + '/bower_components/text/text',
            util: 'public/util/util',
            MINT: SERVER_PATH + '/fe_components/mobile/MINT/index',
            axios: SERVER_PATH + '/bower_components/vue2/axios.min',
            'emap-mobile': SERVER_PATH + '/fe_components/mobile/emap-mobile.min',
            BH_MOBILE: SERVER_PATH + '/fe_components/mobile/BH_MIXIN_SDK',
//            WEIXIN: 'https://res.wx.qq.com/open/js/jweixin-1.2.0',
            selectRoleIndex: '../../itpub/public/mob/component/selectrole/selectrole',
            home: '../../itpub/public/mob/component/home/home',
            spriteUtils: '../../itpub/public/mob/js/spriteUtil',
            publicVueComponent: '../../itpub/public/mob/component',
            'draggable': SERVER_PATH + "/bower_components/vuedraggable/vuedraggable",
            'sortable': SERVER_PATH + "/bower_components/sortable/1.5.1/Sortable.min",
//            qrcode: SERVER_PATH + '/bower_components/qrcode.js/qrcode.js',
//            bhFillStyle: SERVER_PATH + '/fe_components/mobile/bh_utils_mobile',
            emapMin: '../../itpub/public/mob/js/emapMin',
            pagelog: SERVER_PATH + '/fe_components/sentry/sentry.min',
            cropper:SERVER_PATH +'/bower_components/cropper/cropper.min'
        },
        shim: {
//            'qrcode': {
//                exports: 'QRCode'
//            },
//            'bhFillStyle': {
//                exports: 'bhFillStyle'
//            },
        	'emap-mobile': {
                deps: ['jquery'],
            },
            'emapMin': {
                deps: ['jquery'],
            },
            'pagelog': {
                deps: ['jquery']
            }
        },
        waitSeconds: 0

    };

    /**
     * appLoadAsync用于控制app页面的加载方式
     * true: app的所有页面为异步加载，只在使用到时加载
     * false: app的所有页面在应用初始化时一次性加载
     */
    window.appLoadAsync = false;

    //默认的组件库和公共方法以及公共页面
    var requir_default_arr = ['jquery', 'vue', 'vueRouter', 'MINT', 'emap-mobile', 'axios', 'spriteUtils', 'draggable',  'emapMin', 'pagelog','cropper'];

    //封装的公共vue组件
    var default_component_arr = [{
        name: 'auditProcess',
        jsPath: 'publicVueComponent/auditprocess/auditProcess'
    }, {
        name: 'noneDatas',
        jsPath: 'publicVueComponent/nonedatas/nonedatas'
    }];

    /**
     * 用于保存所有模块的全局对象：
     * defaultModules：默认的组件库和公共方法以及公共页面
     * pageModules：当前应用的所有页面模块
     * defaultComponents：封装的公共vue组件
     */
    window.REQUIRE_MODULES_ARR = {
        defaultModules: requir_default_arr,
        pageModules: [],
        defaultComponents: default_component_arr
    };

    //配置require
    require.config(requireConfig);

    //加载框架所需的库和公共页面
    require(requir_default_arr, function($, Vue, VueRouter, mintUI, EMAP_MOBILE, axios, sprite, draggable,  emapMin) {

        //设置拖拽组件
        Vue.component('draggable', draggable);

        //将各个组件库输出到全局作用域
        window.axios = axios;
        window.Vue = Vue;
        window.VueRouter = VueRouter;
        window.mintUI = mintUI;
        window.EMAP_MOBILE = EMAP_MOBILE;
        window.WIS_EMAP_SERV = emapMin;

        //vue路由组件
        Vue.use(VueRouter);
        //饿了么移动端组件mint-ui
        Vue.use(mintUI);
        //EMAP相关vue组件
        Vue.use(EMAP_MOBILE);

        //ids认证
        if (userId != null && userId != undefined) {
            //获取角色配置相关参数 --> 获取用户授权功能 --> 初始化应用
            sprite.getSelRoleConfig().then(sprite.getAuthConfig).then(sprite.initApp);
        }
        //游客
        else {
            // 初始化应用--游客模式
            sprite.initApp_visitor();
        }

    });

}(require));