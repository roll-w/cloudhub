# Http API返回参考

统一返回体：
```json5
{
  "status": 200,// Http状态码
  "message": "SUCCESS",// 若发生错误，具体的错误信息
  "errorCode": "00000",// 错误码。00000代表成功处理
  "data": {
    // DATA
  }
}
```

