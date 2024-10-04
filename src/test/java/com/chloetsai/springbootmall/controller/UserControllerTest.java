package com.chloetsai.springbootmall.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.chloetsai.springbootmall.dao.UserDao;
import com.chloetsai.springbootmall.dto.UserLoginRequest;
import com.chloetsai.springbootmall.dto.UserRegisterRequest;
import com.chloetsai.springbootmall.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDao userDao;

    private ObjectMapper objectMapper = new ObjectMapper();

    // 註冊新帳號
    @Test
    public void register_success() throws Exception {
        // 建立一個 UserRegisterRequest 物件並設置 Email 和密碼
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setEmail("test1@gmail.com");
        userRegisterRequest.setPassword("123");

        // 將 userRegisterRequest 物件轉換為 JSON 格式
        String json = objectMapper.writeValueAsString(userRegisterRequest);

        // 建立一個 POST 請求到 "/users/register"，並設置請求的內容類型為 JSON，並傳入 JSON 內容
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // 執行請求並檢查以下期望結果：
        // 1. HTTP 狀態碼為 201 (已創建)
        // 2. 回應的 JSON 中有 userId 且不為空
        // 3. 回應的 JSON 中的 email 與預期值 "test1@gmail.com" 相同
        // 4. 回應的 JSON 中有 createdDate 和 lastModifiedDate，且不為空
        mockMvc.perform(requestBuilder)
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.userId", notNullValue()))
                .andExpect(jsonPath("$.email", equalTo("test1@gmail.com")))
                .andExpect(jsonPath("$.createdDate", notNullValue()))
                .andExpect(jsonPath("$.lastModifiedDate", notNullValue()));

        // 檢查資料庫中的密碼是否已加密（不應與原始明碼相同）
        User user = userDao.getUserByEmail(userRegisterRequest.getEmail());
        assertNotEquals(userRegisterRequest.getPassword(), user.getPassword());
    }


    @Test
    public void register_invalidEmailFormat() throws Exception {
        // 建立一個 UserRegisterRequest 物件，並設置一個格式不正確的 Email 和密碼
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setEmail("3gd8e7q34l9"); // 不符合 Email 格式
        userRegisterRequest.setPassword("123");

        // 將 userRegisterRequest 物件轉換為 JSON 格式
        String json = objectMapper.writeValueAsString(userRegisterRequest);

        // 建立一個 POST 請求到 "/users/register"，設置請求的內容類型為 JSON，並傳入 JSON 內容
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // 執行請求並檢查以下期望結果：
        // 1. HTTP 狀態碼應為 400 (BAD_REQUEST)，表示 Email 格式不正確
        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }


    @Test
    public void register_emailAlreadyExist() throws Exception {
        // 先註冊一個帳號，準備測試 Email 已存在的情況
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setEmail("test2@gmail.com"); // 設置欲註冊的 Email
        userRegisterRequest.setPassword("123"); // 設置密碼

        // 將 userRegisterRequest 物件轉換為 JSON 格式
        String json = objectMapper.writeValueAsString(userRegisterRequest);

        // 建立一個 POST 請求到 "/users/register"，設置請求的內容類型為 JSON，並傳入 JSON 內容
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // 執行第一次註冊請求並檢查 HTTP 狀態碼應為 201 (已創建)，表示註冊成功
        mockMvc.perform(requestBuilder)
                .andExpect(status().is(201));

        // 再次使用相同的 Email 進行註冊請求，預期會因 Email 重複而返回 400 (BAD_REQUEST)
        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }


    // 登入
    @Test
    public void login_success() throws Exception {
        // 先註冊新帳號，確保用戶存在以測試登入功能
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setEmail("test3@gmail.com");
        userRegisterRequest.setPassword("123");

        // 呼叫註冊方法來註冊新帳號
        register(userRegisterRequest);

        // 準備登入請求，使用與註冊相同的 Email 和密碼
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setEmail(userRegisterRequest.getEmail());
        userLoginRequest.setPassword(userRegisterRequest.getPassword());

        // 將 userLoginRequest 物件轉換為 JSON 格式
        String json = objectMapper.writeValueAsString(userLoginRequest);

        // 建立一個 POST 請求到 "/users/login"，設置請求的內容類型為 JSON，並傳入 JSON 內容
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // 執行登入請求並檢查以下期望結果：
        // 1. HTTP 狀態碼應為 200 (OK)，表示登入成功
        // 2. 回應的 JSON 中有 userId，且不為空
        // 3. 回應的 JSON 中的 email 與登入請求的 Email 相同
        // 4. 回應的 JSON 中有 createdDate 和 lastModifiedDate，且不為空
        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.userId", notNullValue()))
                .andExpect(jsonPath("$.email", equalTo(userRegisterRequest.getEmail())))
                .andExpect(jsonPath("$.createdDate", notNullValue()))
                .andExpect(jsonPath("$.lastModifiedDate", notNullValue()));
    }


    @Test
    public void login_wrongPassword() throws Exception {
        // 先註冊新帳號，準備測試錯誤密碼的情況
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setEmail("test4@gmail.com");
        userRegisterRequest.setPassword("123");

        // 呼叫註冊方法來創建新帳號
        register(userRegisterRequest);

        // 準備登入請求，使用註冊時的 Email，但提供錯誤的密碼
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setEmail(userRegisterRequest.getEmail());
        userLoginRequest.setPassword("unknown"); // 設置錯誤的密碼

        // 將 userLoginRequest 物件轉換為 JSON 格式
        String json = objectMapper.writeValueAsString(userLoginRequest);

        // 建立一個 POST 請求到 "/users/login"，設置請求的內容類型為 JSON，並傳入 JSON 內容
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // 執行登入請求並檢查以下期望結果：
        // 1. HTTP 狀態碼應為 400 (BAD_REQUEST)，表示密碼錯誤
        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }


    @Test
    public void login_invalidEmailFormat() throws Exception {
        // 建立一個 UserLoginRequest 物件，並設置一個格式不正確的 Email 和密碼
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setEmail("hkbudsr324"); // 不符合 Email 格式
        userLoginRequest.setPassword("123");

        // 將 userLoginRequest 物件轉換為 JSON 格式
        String json = objectMapper.writeValueAsString(userLoginRequest);

        // 建立一個 POST 請求到 "/users/login"，設置請求的內容類型為 JSON，並傳入 JSON 內容
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // 執行登入請求並檢查以下期望結果：
        // 1. HTTP 狀態碼應為 400 (BAD_REQUEST)，表示 Email 格式無效
        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }


    @Test
    public void login_emailNotExist() throws Exception {
        // 建立一個 UserLoginRequest 物件，並設置一個不存在的 Email 和密碼
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setEmail("unknown@gmail.com"); // 使用一個不存在的 Email
        userLoginRequest.setPassword("123");

        // 將 userLoginRequest 物件轉換為 JSON 格式
        String json = objectMapper.writeValueAsString(userLoginRequest);

        // 建立一個 POST 請求到 "/users/login"，設置請求的內容類型為 JSON，並傳入 JSON 內容
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // 執行登入請求並檢查以下期望結果：
        // 1. HTTP 狀態碼應為 400 (BAD_REQUEST)，表示該 Email 不存在
        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }


    private void register(UserRegisterRequest userRegisterRequest) throws Exception {
        String json = objectMapper.writeValueAsString(userRegisterRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(201));
    }
}