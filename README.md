##CSignals插件使用说明
* 版本:1.3.1

##环境配置
* npm 4.4.1 +
* node 9.8.0 +


##使用流程 

######1.进入项目的根目录，添加热更新插件:com.chinamobile.sig.csignals
* 为项目添加CSignals插件，执行:`cordova plugin add com.chinamobile.sig.csignals`
* 如果要删除插件,执行:`cordova plugin add com.chinamobile.sig.CSignals`
* 为项目添加对应的platform平台,已添加过，此步忽略，执行:
* 安卓平台: `cordova platform add android`
* 将插件添加到对应平台,执行: `cordova build`

######2.在js文件中,通过以下js方法调用插件，获取LTE信息数据
*
```javascript
   location: function(){
        //向native发出LTE信息监听请求
        cordova.exec(success,error,"Location","coolMethod",[]);
    }
    
     success: function(var result){
        //LAC
        var lac = result[0];
        //CI
        var ci  = result[1];
        //PCI
        var pci = result[2];
        //RSRP
        var rsrp = result[3];
        //SINR
        var sinr = result[4];
        //CQI
        var cqi = result[5];
        //RSSNR
        var rssnr = result[6];
        //RSRQ
        var rsrq = result[7];
        //EVDO_SNR
        var evdo_snr = result[8];
        //LEVEL
        var level = result[9];
        //ASU_LEVEL
        var asu_level = result[10];
        //TIMING_ADVANCE
        var timing_advance = result[11];
        //DBM
        var dbm = result[12];
    }

    error: function(var result){
        //LTE信息获取异常提示信息
        alert(result);
    }
```
######说明:
* 1.success函数:result是一个数组,元素0：LAC，元素1：CI,元素2：PCI，元素3：RSRP，元素4：SINR，
* 元素5:CQI,元素6:RSSNR,元素7：RSRQ，元素8：EVDO_SNR，元素9：LEVEL，元素10：ASU_LEVEL，元素11：TIMING_ADVANCE
* 元素12：DBM


##问题反馈
在使用中有任何问题，可以用以下联系方式.

* 邮件:18520660170@139.com
* 时间:2018-5-24 16:00:00