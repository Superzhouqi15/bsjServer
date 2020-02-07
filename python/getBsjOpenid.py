import requests
import sys
import json

appId = "wxc185ccb812430354"
secret = "3431ba2db21dd03001fff5657868e89d"

urlName = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId + \
          "&secret=" + secret + "&js_code=" + sys.argv[1] + "&grant_type=authorization_code"
if __name__ == '__main__':
    response = requests.get(urlName)
    print (json.loads(response.text).get("openid"))