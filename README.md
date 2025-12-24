# Android 身份证 OCR 识别系统（基于腾讯云 OCR）

## 一、项目简介

本项目是一个基于 Android Studio 开发的身份证识别应用。  
通过调用腾讯云 OCR 身份证识别接口（IDCardOCR，API v3 签名方式），  
实现从手机相册选择身份证图片，并自动识别身份证中的关键信息。

项目完整实现了从 Android 客户端到云端 OCR 服务的完整调用流程，包括：

- Android 用户界面设计
- 图片选择与 Base64 编码
- 腾讯云 API v3 签名算法（非 SDK，手动实现）
- OkHttp 网络请求
- JSON 数据解析
- OCR 识别结果展示

适用于 Android 开发课程设计、实验报告以及 OCR 技术学习示例。

---

## 二、功能特性

- 从手机相册选择身份证照片
- 支持身份证正面与反面识别
- 使用腾讯云 API v3 签名算法进行身份认证
- 通过 OkHttp 发送 HTTPS 请求
- 解析并处理 OCR 返回的 JSON 数据
- 在界面中展示姓名、身份证号、性别、民族、住址等信息
- 支持 Android 模拟器与真机运行

---

## 三、技术栈

- 开发语言：Java  
- 开发工具：Android Studio  
- 网络请求：OkHttp 3  
- OCR 服务：腾讯云 OCR（IDCardOCR）  
- 签名方式：腾讯云 API v3  
- 数据格式：JSON  
- 最低支持系统：Android API 24  

---

## 四、应用流程说明

1. 启动应用，进入主界面  
2. 点击“选择身份证图片”，从相册中选择照片  
3. 点击“开始识别”  
4. 应用向腾讯云 OCR 接口发送请求  
5. 接收并解析识别结果  
6. 在界面中展示身份证信息  

---

## 五、使用前准备

### 5.1 开通腾讯云 OCR 服务

1. 注册腾讯云账号  
2. 开通 OCR 服务中的“身份证识别（IDCardOCR）”  
3. 获取以下密钥信息：
   - SecretId
   - SecretKey

---

### 5.2 本地密钥配置（重要）

本项目不会在源码中明文存储任何密钥信息。  
密钥通过 `local.properties + BuildConfig` 的方式注入，仅存在于本地环境。

#### （1）创建 local.properties 文件

在项目根目录创建 `local.properties` 文件，内容如下：

```properties
sdk.dir=D\:\\AndroidSDK

TENCENT_SECRET_ID=你的SecretId
TENCENT_SECRET_KEY=你的SecretKey
```
说明：

- `local.properties` 已被加入 `.gitignore`
- 该文件不会被提交到 GitHub
- 每台电脑需要单独配置自己的密钥

---

#### （2）Gradle 注入到 BuildConfig

在 `app/build.gradle` 中读取并注入密钥：

```gradle
buildConfigField "String",
        "TENCENT_SECRET_ID",
        "\"${localProps['TENCENT_SECRET_ID']}\""

buildConfigField "String",
        "TENCENT_SECRET_KEY",
        "\"${localProps['TENCENT_SECRET_KEY']}\""
```
#### （3）代码中安全使用密钥

在 `OcrApi.java` 中通过 `BuildConfig` 读取密钥：

```java
private static final String SECRET_ID = BuildConfig.TENCENT_SECRET_ID;
private static final String SECRET_KEY = BuildConfig.TENCENT_SECRET_KEY;
```
源码中不包含任何真实密钥信息，可安全公开仓库。

---

## 六、运行项目

1. 克隆本项目仓库  
2. 在项目根目录创建并配置 `local.properties`  
3. 使用 Android Studio 同步 Gradle  
4. 运行项目到模拟器或真机  

---

## 七、安全说明

- 请勿将 `SecretId` 或 `SecretKey` 写入 Java 源码  
- 请勿提交 `local.properties` 文件到 GitHub  
- 使用 `.gitignore + BuildConfig` 是推荐的安全做法  

---

## 八、适用说明

- 本项目仅用于学习、教学和课程实验用途  
- OCR 接口调用会产生腾讯云 API 调用费用，请注意账户余额（每月免费一千次）  

---

## 九、License

本项目仅供学习与交流使用


# 更新说明 v1.2

**更新时间**：2025-12-24

## 更新内容

### 1. 新增功能：拍照识别身份证
- 增加“拍照”按钮，用户可直接用摄像头拍摄身份证进行识别。
- 无需提前准备图片文件，提高使用便利性。

### 2. 自动正反面识别
- 系统会先尝试识别身份证正面 (`FRONT`)。
- 若正面信息为空或识别失败，自动尝试识别身份证背面 (`BACK`)。
- 避免用户手动切换正反面，提高识别体验。

### 3. 详细异常输出
- 在 OCR 调用异常或解析失败时，界面显示：
  - 异常类型  
  - 异常信息  
  - 完整堆栈信息  
- 方便开发者调试和排查问题。


## 注意事项
- 每次调用腾讯云 OCR 接口都会消耗账号额度，即使识别失败也算一次调用。
- 为避免不必要消耗，请确保上传的身份证照片清晰、完整，并尽量先区分正反面。
