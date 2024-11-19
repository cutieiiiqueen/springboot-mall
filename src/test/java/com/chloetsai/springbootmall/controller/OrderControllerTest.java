package com.chloetsai.springbootmall.controller;

import com.chloetsai.springbootmall.dto.BuyItem;
import com.chloetsai.springbootmall.dto.CreateOrderRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    // 自動注入 MockMvc 用於模擬 HTTP 請求
    @Autowired
    private MockMvc mockMvc;

    // 用於將 Java 物件轉換為 JSON 的工具
    private ObjectMapper objectMapper = new ObjectMapper();

    // 測試方法：測試創建訂單成功的情況
    @Transactional // 確保測試過程中產生的資料會在測試結束後回滾
    @Test
    public void createOrder_success() throws Exception {
        // 建立請求物件 CreateOrderRequest 並設定其內容
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        List<BuyItem> buyItemList = new ArrayList<>(); // 訂購商品的清單

        // 設定第一個商品的 ID 和數量
        BuyItem buyItem1 = new BuyItem();
        buyItem1.setProductId(1); // 商品 ID
        buyItem1.setQuantity(5);  // 購買數量
        buyItemList.add(buyItem1); // 將商品加入清單

        // 設定第二個商品的 ID 和數量
        BuyItem buyItem2 = new BuyItem();
        buyItem2.setProductId(2); // 商品 ID
        buyItem2.setQuantity(2);  // 購買數量
        buyItemList.add(buyItem2); // 將商品加入清單

        // 將商品清單設定到 CreateOrderRequest 中
        createOrderRequest.setBuyItemList(buyItemList);

        // 將 Java 物件轉換為 JSON 字串，作為 HTTP 請求的內容
        String json = objectMapper.writeValueAsString(createOrderRequest);

        // 構建模擬的 POST 請求
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/{userId}/orders", 1) // 目標 URL，包含路徑參數 userId
                .contentType(MediaType.APPLICATION_JSON) // 設定內容類型為 JSON
                .content(json); // 設定請求的 JSON 內容

        // 執行模擬請求並進行結果驗證
        mockMvc.perform(requestBuilder)
                .andExpect(status().is(201)) // 驗證 HTTP 狀態碼為 201 Created
                .andExpect(jsonPath("$.orderId", notNullValue())) // 驗證回應中的 orderId 不為空
                .andExpect(jsonPath("$.userId", equalTo(1))) // 驗證回應中的 userId 為 1
                .andExpect(jsonPath("$.totalAmount", equalTo(750))) // 驗證總金額為 750
                .andExpect(jsonPath("$.orderItemList", hasSize(2))) // 驗證訂單項目數量為 2
                .andExpect(jsonPath("$.createdDate", notNullValue())) // 驗證創建日期不為空
                .andExpect(jsonPath("$.lastModifiedDate", notNullValue())); // 驗證最後修改日期不為空
    }

    // 測試：創建訂單時，BuyItemList 為空的情況，應返回 400 Bad Request
    @Transactional
    @Test
    public void createOrder_illegalArgument_emptyBuyItemList() throws Exception {
        // 建立 CreateOrderRequest，BuyItemList 設為空
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        List<BuyItem> buyItemList = new ArrayList<>();
        createOrderRequest.setBuyItemList(buyItemList);

        // 將 CreateOrderRequest 轉換為 JSON
        String json = objectMapper.writeValueAsString(createOrderRequest);

        // 構建模擬請求
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/{userId}/orders", 1) // 使用存在的 userId
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // 驗證回應狀態碼為 400 Bad Request
        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }

    // 測試：當用戶不存在時創建訂單，應返回 400 Bad Request
    @Transactional
    @Test
    public void createOrder_userNotExist() throws Exception {
        // 建立含有一個商品的 CreateOrderRequest
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        List<BuyItem> buyItemList = new ArrayList<>();

        BuyItem buyItem1 = new BuyItem();
        buyItem1.setProductId(1); // 商品 ID
        buyItem1.setQuantity(1);  // 購買數量
        buyItemList.add(buyItem1);

        createOrderRequest.setBuyItemList(buyItemList);

        // 將 CreateOrderRequest 轉換為 JSON
        String json = objectMapper.writeValueAsString(createOrderRequest);

        // 使用不存在的 userId 構建請求
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/{userId}/orders", 100) // 假設 100 為不存在的用戶 ID
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // 驗證回應狀態碼為 400 Bad Request
        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }

    // 測試：當商品不存在時創建訂單，應返回 400 Bad Request
    @Transactional
    @Test
    public void createOrder_productNotExist() throws Exception {
        // 建立含有不存在商品的 CreateOrderRequest
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        List<BuyItem> buyItemList = new ArrayList<>();

        BuyItem buyItem1 = new BuyItem();
        buyItem1.setProductId(100); // 假設 100 為不存在的商品 ID
        buyItem1.setQuantity(1);
        buyItemList.add(buyItem1);

        createOrderRequest.setBuyItemList(buyItemList);

        // 將 CreateOrderRequest 轉換為 JSON
        String json = objectMapper.writeValueAsString(createOrderRequest);

        // 構建請求
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/{userId}/orders", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // 驗證回應狀態碼為 400 Bad Request
        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }

    // 測試：當商品庫存不足時創建訂單，應返回 400 Bad Request
    @Transactional
    @Test
    public void createOrder_stockNotEnough() throws Exception {
        // 建立含有超出庫存數量的商品的 CreateOrderRequest
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        List<BuyItem> buyItemList = new ArrayList<>();

        BuyItem buyItem1 = new BuyItem();
        buyItem1.setProductId(1);
        buyItem1.setQuantity(10000); // 假設超出庫存數量
        buyItemList.add(buyItem1);

        createOrderRequest.setBuyItemList(buyItemList);

        // 將 CreateOrderRequest 轉換為 JSON
        String json = objectMapper.writeValueAsString(createOrderRequest);

        // 構建請求
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/{userId}/orders", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // 驗證回應狀態碼為 400 Bad Request
        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }

    // 測試：查詢指定用戶的訂單列表
    @Test
    public void getOrders() throws Exception {
        // 構建 GET 請求
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/users/{userId}/orders", 1);

        // 驗證回應狀態碼為 200 並檢查結果內容
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit", notNullValue())) // 驗證分頁限制值存在
                .andExpect(jsonPath("$.offset", notNullValue())) // 驗證偏移量存在
                .andExpect(jsonPath("$.total", notNullValue())) // 驗證總數存在
                .andExpect(jsonPath("$.results", hasSize(2))) // 驗證結果清單大小為 2
                .andExpect(jsonPath("$.results[0].orderId", notNullValue())) // 驗證第一個訂單的 ID
                .andExpect(jsonPath("$.results[0].userId", equalTo(1))) // 驗證用戶 ID
                .andExpect(jsonPath("$.results[0].totalAmount", equalTo(100000))) // 驗證第一個訂單金額
                .andExpect(jsonPath("$.results[0].orderItemList", hasSize(1))) // 驗證第一個訂單的項目數量
                .andExpect(jsonPath("$.results[0].createdDate", notNullValue())) // 驗證創建日期
                .andExpect(jsonPath("$.results[0].lastModifiedDate", notNullValue())) // 驗證最後修改日期
                .andExpect(jsonPath("$.results[1].orderId", notNullValue()))
                .andExpect(jsonPath("$.results[1].userId", equalTo(1)))
                .andExpect(jsonPath("$.results[1].totalAmount", equalTo(500690)))
                .andExpect(jsonPath("$.results[1].orderItemList", hasSize(3)))
                .andExpect(jsonPath("$.results[1].createdDate", notNullValue()))
                .andExpect(jsonPath("$.results[1].lastModifiedDate", notNullValue()));
    }

    // 測試：分頁查詢訂單
    @Test
    public void getOrders_pagination() throws Exception {
        // 構建 GET 請求，帶有分頁參數
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/users/{userId}/orders", 1)
                .param("limit", "2")
                .param("offset", "2");

        // 驗證回應內容
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit", notNullValue())) // 驗證分頁限制值存在
                .andExpect(jsonPath("$.offset", notNullValue())) // 驗證偏移量存在
                .andExpect(jsonPath("$.total", notNullValue())) // 驗證總數存在
                .andExpect(jsonPath("$.results", hasSize(0))); // 驗證結果清單大小為 0
    }

    // 測試：查詢沒有訂單的用戶
    @Test
    public void getOrders_userHasNoOrder() throws Exception {
        // 構建 GET 請求
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/users/{userId}/orders", 2);

        // 驗證回應內容
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit", notNullValue()))
                .andExpect(jsonPath("$.offset", notNullValue()))
                .andExpect(jsonPath("$.total", notNullValue()))
                .andExpect(jsonPath("$.results", hasSize(0))); // 驗證結果清單大小為 0
    }

    // 測試：查詢不存在的用戶
    @Test
    public void getOrders_userNotExist() throws Exception {
        // 構建 GET 請求
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/users/{userId}/orders", 100); // 假設 100 為不存在的用戶 ID

        // 驗證回應內容
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit", notNullValue()))
                .andExpect(jsonPath("$.offset", notNullValue()))
                .andExpect(jsonPath("$.total", notNullValue()))
                .andExpect(jsonPath("$.results", hasSize(0))); // 驗證結果清單大小為 0
    }
}
