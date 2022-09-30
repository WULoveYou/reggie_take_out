package com.qingAn.reggie.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;

/**
 * 短信发送工具类
 */
public class SMSUtils {

	/**
	 * 发送短信
	 * @param signName 签名
	 * @param templateCode 模板
	 * @param phoneNumbers 手机号
	 * @param param 参数
	 */
	public static void sendMessage(String signName, String templateCode,String phoneNumbers,String param){
		/*
		 * accessKeyId 密钥id
		 * secret 密钥值
		 */
		DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAIctmB3sD7cWEQ", "79lRVTR1sXurdEGTKlvi2yyWkm5jCR");
		IAcsClient client = new DefaultAcsClient(profile);

		SendSmsRequest request = new SendSmsRequest();
		request.setSysRegionId("cn-hangzhou");
		request.setPhoneNumbers(phoneNumbers);
		request.setSignName(signName);
		request.setTemplateCode(templateCode);
		request.setTemplateParam("{\"code\":\""+param+"\"}");
		try {
			SendSmsResponse response = client.getAcsResponse(request);
			System.out.println("短信发送成功:"+response.getMessage());
		}catch (ClientException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String code = ValidateCodeUtils.generateValidateCode(4).toString();
		System.out.println("验证码："+code);
		SMSUtils.sendMessage("阿里云短信测试","SMS_154950909","15922750321",code);
	}
}
