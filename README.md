##CSignals���ʹ��˵��
* �汾:1.3.1

##��������
* npm 4.4.1 +
* node 9.8.0 +


##ʹ������ 

######1.������Ŀ�ĸ�Ŀ¼������ȸ��²��:com.chinamobile.sig.csignals
* Ϊ��Ŀ���CSignals�����ִ��:`cordova plugin add com.chinamobile.sig.csignals`
* ���Ҫɾ�����,ִ��:`cordova plugin add com.chinamobile.sig.CSignals`
* Ϊ��Ŀ��Ӷ�Ӧ��platformƽ̨,����ӹ����˲����ԣ�ִ��:
* ��׿ƽ̨: `cordova platform add android`
* �������ӵ���Ӧƽ̨,ִ��: `cordova build`

######2.��js�ļ���,ͨ������js�������ò������ȡLTE��Ϣ����
*
```javascript
   location: function(){
        //��native����LTE��Ϣ��������
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
        //LTE��Ϣ��ȡ�쳣��ʾ��Ϣ
        alert(result);
    }
```
######˵��:
* 1.success����:result��һ������,Ԫ��0��LAC��Ԫ��1��CI,Ԫ��2��PCI��Ԫ��3��RSRP��Ԫ��4��SINR��
* Ԫ��5:CQI,Ԫ��6:RSSNR,Ԫ��7��RSRQ��Ԫ��8��EVDO_SNR��Ԫ��9��LEVEL��Ԫ��10��ASU_LEVEL��Ԫ��11��TIMING_ADVANCE
* Ԫ��12��DBM


##���ⷴ��
��ʹ�������κ����⣬������������ϵ��ʽ.

* �ʼ�:18520660170@139.com
* ʱ��:2018-5-24 16:00:00