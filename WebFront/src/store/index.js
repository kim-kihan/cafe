import Vue from 'vue'
import Vuex from 'vuex'
import http from '@/util/http-common'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    user: {
      id: '',
      name: '',
      pass: '',
      stamps: 0,
      age: '',
      gender: ''
    },
    loginUser: {
      id: '',
      name: '',
      pass: '',
      stamps: 0,
      age: '',
      gender: ''
    },
    products: [],
    temp_products: [],
    orders: [],
    orderDetails: [],
    comments: [],
    all_comments: [],
    bol: false,
    stamps: [],
  },
  getters: {
    getUser(state) {
      return state.user;
    },
    getLoginUser(state) {
      return state.loginUser;
    },
    getProducts(state) {
      return state.products;
    },
    getTempProducts(state) {
      return state.temp_products;
    },
    getOrders(state) {
      return state.orders;
    },
    getOrderDetails(state) {
      return state.orderDetails;
    },
    getComments(state) {
      return state.comments;
    },
    getBol(state) {
      return state.bol
    }
  },

  mutations: {
    INSERT_USER(state, user) {
      state.user.id = user.id;
      state.user.name = user.name;
      state.user.pass = user.pass;
      state.user.stamps = user.stamps;
      state.user.age = user.age;
      state.user.gender = user.gender;
    },
    LOGIN_USER(state, loginUser) {
      state.loginUser.id = loginUser.id;
      state.loginUser.name = loginUser.name;
      state.loginUser.pass = loginUser.pass;
      state.loginUser.stamps = loginUser.stamps;
      state.loginUser.age = loginUser.age;
      state.loginUser.gender = loginUser.gender;
    },
    SELECT_PRODUCT(state, products) {
      state.products = products;
    },
    SELECT_TEMP_PRODUCTS(state, products) {
      state.temp_products = products;
    },
    SELECT_ORDER(state, orders) {
      state.orders = orders;
    },
    SELECT_DETAIL_ORDER(state, orders) {
      state.orderDetails = orders;
    },
    SELECT_COMMENTS(state, comment) {
      state.comments = comment;
    },
    ADD_COMMENTS(state, data) {
      state.comments.push(data);  
    },
    UPDATE_COMMENTS(state, data) {
      let newComments = [];
      let comments = state.comments;

      for (let c of comments) {
        if (c.id === data.id) {
          c.rating = data.rating;
          c.comment = data.comment;
        }
        newComments.push(c);
      }
      state.comments = newComments;
    },
    DELETE_COMMENTS(state, data) {
      let newComments = [];
      let comments = state.comments;

      for (let c of comments) {
        if (c.id !== data) {
          newComments.push(c);
        }
      }
      state.comments = newComments;
    },
    INSERT_USER_CHECK(state, bol) {
      state.bol = bol;
    },
    INSERT_STAMP(state, data) {
      state.stamps.push(data);  
    },
  },

  actions: {
    //사용자 수정
    updateUser(context, payload) {
         http.put('/user', payload)
           .then(({ data }) => {
             console.log("index data" + data)
             context.commit("LOGIN_USER", data);
           })
           .catch((err) => {
             console.log(err);
           })
   },
    // 아이디 중복 체크
    checkUser({ commit }, payload) {
      return new Promise((resolve) => {
        setTimeout(() => {        
          http.get(`/user/isUsed?id=${payload}`)          
            .then((response) => {
            let bol = response.data;
            commit('INSERT_USER_CHECK', bol);
            resolve()
            }, 1000)
            .catch((err) => {
              console.log(err);
          })
        })
      }) 
    },
    // 회원가입 기능
    insertUser(context, payload) {
      http.post('/user', payload)
        .then(({ data }) => {
          context.commit('INSERT_USER', data);
          alert("회원가입 성공");
        })
        .catch((error) => { // 가입 실패 시 
          console.log(error);
        })
    },
     // 회원 로그인 기능
    login(context, payload) {
      http.post('/user/login', payload.data)
        .then((response) => {
          context.commit('LOGIN_USER', response.data);
          if (response.data === "") {
            alert("로그인 실패입니다. 아이디와 비밀번호를 확인하세요.");            
          } else {
            alert("로그인 성공")
            payload.callback();            
          }
          
        })
        .catch((err) => { // 로그인 실패
          console.log(err);
          alert("로그인 실패입니다. 아이디와 비밀번호를 확인하세요.");
      })
    },
    resetLoginUser(context) {
      http.get('/user/logout')
        .then((response) => {
          context.commit('LOGIN_USER', response.data);
          alert("로그아웃 되었습니다.");
        })
        .catch((err) => {
          console.log(err);
      })
    },
    selectProducts(context) {
      // 전체 상품 목록 출력
      return new Promise((resolve) => {
        setTimeout(() => {
          http.get('/product')
          .then(({ data }) => {
            let temp = [];
            context.commit('SELECT_TEMP_PRODUCTS', temp);
            context.commit('SELECT_PRODUCT', data);
            resolve()
          }, 1000)
          .catch((err) => {
            console.log(err);
          })
        })
      }) 
    },
    selectOrders(context, payload) {
      return new Promise((resolve) => {
        setTimeout(() => {
          http.get(`/order/byUser?id=${payload}`)
          .then(({ data }) => {
            context.commit('SELECT_ORDER', data);
            resolve()
          }, 1000)
          .catch((err) => {
            console.log(err);
          })
        })
      }) 
    },
    makeOrder({ commit }, order) {
      let t_order = JSON.parse(localStorage.getItem('t_order'));
      // localStorage에서 가져온 상품 정보 전달
      let orders = [];
      for (let o of t_order) {
        orders.push(o);
      }
      orders.push(order);

      localStorage.setItem("t_order", JSON.stringify(orders));
      localStorage.removeItem('temp_products');
      
      let products = [];
      commit('SELECT_TEMP_PRODUCTS', products);
      commit('SELECT_ORDER', orders);
    },

    // 주문 상세 정보 불러오기
    selectOrderDetails(context, payload) {
      http.get(`/order/${payload}`)
        .then(({ data }) => { 
          context.commit('SELECT_DETAIL_ORDER', data);
        })
        .catch((err) => {
          console.log(err);
        })
    },

    // 주문 상세 정보 전달 / stamps t_user 에 추가하는 기능도 추후 구현
    makeOrderDetail(context, payload) {
      http.post('/order', payload)
        .then(({ data }) => {
          let totalQuantity = 0;
          for (let detail of payload.details) {
            detail.orderId = data
            totalQuantity += detail.quantity;
            http.post('/order/orderdetail', detail)
              .then(({ data }) => {              
              console.log(data)
              let temp = [];
              context.commit('SELECT_TEMP_PRODUCTS', temp);        
            })
            .catch((err) => {
              console.log(err);
            })
          }
          let temp = [];
          context.commit('SELECT_TEMP_PRODUCTS', temp);     
          
          payload.stamps.orderId = data
          payload.stamps.userId = payload.userId
          payload.stamps.quantity = totalQuantity
          
          http.post('/stamp', payload.stamps)
          .then(({ data }) => {
            context.commit('INSERT_STAMP', data); 
            payload.user.id = payload.userId
            payload.user.stamps = totalQuantity
            
            http.put('/user', payload.user)
             .then(({ data }) => {
               context.commit("LOGIN_USER", data);
               http.post('/user/login', payload.form)
             .then((response) => {
               context.commit('LOGIN_USER', response.data);              
             })
             .catch((err) => { 
               console.log(err);
           })
             })
             .catch((err) => {
               console.log(err);
             })
            
             
          })
          .catch((error) => { 
            console.log(error);
          })
        })
        .catch((err) => {
          console.log(err);
          alert("주문 실패");
        })
        alert("주문 성공")
    },
    addTempProducts({ commit }, product) {
      let temp_product = JSON.parse(localStorage.getItem('temp_products'));
      
      let temp_products = []
      let flag = false;

      if (temp_product) { // 이미 담은 경우
        for (let t of temp_product) {
          if (t.id === product.id) {
            t.cnt++;
            flag = true;
          }
          temp_products.push(t);
        }
        // 같은 게 없는 경우
        if (!flag) {
          temp_products.push(product);
        }
      }
      else { // 처음으로 담는 경우
        temp_products.push(product);
      }
      
      localStorage.setItem('temp_products', JSON.stringify(temp_products));
      commit("SELECT_TEMP_PRODUCTS", temp_products);
    },
    removeTempProducts({ commit }, product) {
      let temp_product = JSON.parse(localStorage.getItem('temp_products'));
      
      let temp_products = []

      for (let t of temp_product) {
        if (t.id === product.id) {
          t.cnt--;
          if (t.cnt === 0) continue;
        }
        temp_products.push(t);
      }

      localStorage.setItem('temp_products', JSON.stringify(temp_products));
      commit("SELECT_TEMP_PRODUCTS", temp_products);
    },
    selectComments(context, payload) {
      //상품 id에 해당하는 상품평 목록 가져오기
      return new Promise((resolve) => {
        setTimeout(() => {
          http.get(`/comment/${payload}`)
            .then(({ data }) => {
              context.commit("SELECT_COMMENTS", data);
              resolve();
            }, 1000)
            .catch((err) => {
              console.log(err);
            })
        })
      }) 
    },
    registComment(context, payload) {
      // 상품평 등록하기
      return new Promise((resolve) => {
        setTimeout(() => {
          http.post('/comment', payload)
            .then(({ data }) => {
              context.commit("ADD_COMMENTS", data);
              resolve();
            }, 1000)
            .catch((err) => {
              console.log(err);
            })
        })
      }) 
      
    },
    deleteComment(context, payload) {
      // 상품평 삭제하기
      return new Promise((resolve) => {
        setTimeout(() => {
          http.delete(`/comment/${payload}`)
            .then(({ data }) => {
              context.commit("DELETE_COMMENTS", data);
              resolve();
            }, 1000)
            .catch((err) => {
              console.log(err);
            })
        })
      }) 
    },
    updateComment(context, payload) {
       // 상품평 수정하기
       return new Promise((resolve) => {
        setTimeout(() => {
          http.put('/comment', payload)
            .then(({ data }) => {
              context.commit("UPDATE_COMMENTS", data);
              resolve();
            }, 1000)
            .catch((err) => {
              console.log(err);
            })
        })
      }) 
    },
    // 스탬프 추가
    insertStamp(context, payload) {
      http.post('/stamp', payload)
        .then(({ data }) => {
          context.commit('INSERT_STAMP', data);        
        })
        .catch((error) => { // 가입 실패 시 
          console.log(error);
        })
    },
  },
  modules: {
  }
})
