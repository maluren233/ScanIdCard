# 📇 Android 身份证 OCR 识别系统（基于腾讯云 OCR）

## 📌 项目简介

本项目是一个基于 **Android Studio** 开发的身份证识别应用，  
通过调用 **腾讯云 OCR 身份证识别接口（IDCardOCR，签名方式 v3）**，  
实现从手机相册选择身份证图片，并自动识别身份证中的关键信息。

项目完整实现了：

- Android UI 界面
- 图片选择与 Base64 编码
- 腾讯云 API v3 签名
- OkHttp 网络请求
- JSON 数据解析
- OCR 结果展示

适用于 **Android 开发课程设计 / 实验 / OCR 学习示例**。

---

## ✨ 功能特性

- 📷 从相册选择身份证照片
- 🔄 支持身份证 **正面 / 反面** 识别
- 🔐 使用腾讯云 **v3 签名算法**（非 SDK，手写实现）
- 🌐 使用 **OkHttp3** 发送 HTTPS 请求
- 📄 解析 OCR 返回 JSON 数据
- 🪪 显示姓名、身份证号、性别、民族、住址等信息
- 🧪 支持 Android 模拟器与真机运行

---

## 🛠️ 技术栈

| 模块 | 技术 |
|----|----|
| 开发语言 | Java |
| 开发工具 | Android Studio |
| 网络请求 | OkHttp 3 |
| OCR 服务 | 腾讯云 OCR（IDCardOCR） |
| 签名方式 | 腾讯云 API v3 |
| 数据格式 | JSON |
| 最低支持 | Android API 24 |

---

## 📱 应用界面说明

1. 主界面提供「选择身份证图片」按钮  
2. 用户从相册选择身份证照片  
3. 点击「开始识别」  
4. 应用向腾讯云 OCR 接口发送请求  
5. 返回识别结果并展示在界面中  

---

## 🔑 使用前准备

### 1️⃣ 开通腾讯云 OCR 服务

- 注册腾讯云账号
- 开通 **OCR → 身份证识别**
- 获取：
  - `SecretId`
  - `SecretKey`

---

### 2️⃣ 配置密钥（必须）

在 `OcrApi.java` 中填写你自己的密钥：

```java
private static final String SECRET_ID = "你的SecretId";
private static final String SECRET_KEY = "你的SecretKey";
