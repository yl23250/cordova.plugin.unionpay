var cordova = require('cordova'), exec = require('cordova/exec');
//测试卡号：6226440123456785  密码：111101
/**
 * 
	 navigator.unionpay.pay({
		tn:"201506021533030094142"
	},function(code){
		alert('success:' + code);
	},function(code){
		alert('error:' + code);
	}); 
 */

module.exports = {
    pay: function(orderInfo,onSuccess,onError){
        exec(onSuccess, onError, "Unionpay", "pay", [orderInfo]);
    }
};
