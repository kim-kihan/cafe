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
    //????????? ??????
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
    // ????????? ?????? ??????
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
    // ???????????? ??????
    insertUser(context, payload) {
      http.post('/user', payload)
        .then(({ data }) => {
          context.commit('INSERT_USER', data);
          alert("???????????? ??????");
        })
        .catch((error) => { // ?????? ?????? ??? 
          console.log(error);
        })
    },
     // ?????? ????????? ??????
    login(context, payload) {
      http.post('/user/login', payload.data)
        .then((response) => {
          context.commit('LOGIN_USER', response.data);
          if (response.data === "") {
            alert("????????? ???????????????. ???????????? ??????????????? ???????????????.");            
          } else {
            alert("????????? ??????")
            payload.callback();            
          }
          
        })
        .catch((err) => { // ????????? ??????
          console.log(err);
          alert("????????? ???????????????. ???????????? ??????????????? ???????????????.");
      })
    },
    resetLoginUser(context) {
      http.get('/user/logout')
        .then((response) => {
          context.commit('LOGIN_USER', response.data);
          alert("???????????? ???????????????.");
        })
        .catch((err) => {
          console.log(err);
      })
    },
    selectProducts(context) {
      // ?????? ?????? ?????? ??????
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
      // localStorage?????? ????????? ?????? ?????? ??????
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

    // ?????? ?????? ?????? ????????????
    selectOrderDetails(context, payload) {
      http.get(`/order/${payload}`)
        .then(({ data }) => { 
          context.commit('SELECT_DETAIL_ORDER', data);
        })
        .catch((err) => {
          console.log(err);
        })
    },

    // ?????? ?????? ?????? ?????? / stamps t_user ??? ???????????? ????????? ?????? ??????
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
          alert("?????? ??????");
        })
        alert("?????? ??????")
    },
    addTempProducts({ commit }, product) {
      let temp_product = JSON.parse(localStorage.getItem('temp_products'));
      
      let temp_products = []
      let flag = false;

      if (temp_product) { // ?????? ?????? ??????
        for (let t of temp_product) {
          if (t.id === product.id) {
            t.cnt++;
            flag = true;
          }
          temp_products.push(t);
        }
        // ?????? ??? ?????? ??????
        if (!flag) {
          temp_products.push(product);
        }
      }
      else { // ???????????? ?????? ??????
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
      //?????? id??? ???????????? ????????? ?????? ????????????
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
      // ????????? ????????????
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
      // ????????? ????????????
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
       // ????????? ????????????
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
    // ????????? ??????
    insertStamp(context, payload) {
      http.post('/stamp', payload)
        .then(({ data }) => {
          context.commit('INSERT_STAMP', data);        
        })
        .catch((error) => { // ?????? ?????? ??? 
          console.log(error);
        })
    },
  },
  modules: {
  }
})
