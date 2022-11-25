<template>
  <div class="container">
    <div v-for="(order, idx) in orders" :key="idx">
        <b-list-group>
            <b-list-group-item @click="openPage(idx)">주문 시간 : {{ order.order_time  }}<br>
            </b-list-group-item>
        </b-list-group>        
    </div>


    <hr/>
    <div v-if="show">
        <div v-for="(item, idx) in orderedProduct" :key="idx">
            <b-card
                :img-src="require('@/assets/menu/' + item.img)"
                img-left="img-left"
                img-height="50"
                img-width="50"
                bg-variant="secondary">
                <b-card-text style="display: inline">
                    품명: {{ item.name }}, 단가: {{ item.unitprice }}, {{ item.quantity }}잔
                    <b-button variant="primary" disabled>{{ item.unitprice * item.quantity }}원</b-button>
                </b-card-text>
            </b-card>
        </div>

        <hr/>
        <p>총 비용: {{ getTotalCost() }}원, 스탬프 적립 : {{ getStamps() }}</p>
        <hr/>
    </div>

  </div>
</template>

<script>
import store from "@/store"
import moment from 'moment'
export default {
  name: "order-datail-view",
  data() {
    return {
      orders: [],
      orderDetails: [],
      orderedProduct: [],
      show: false,
    };
  },
  created() {  
    this.$store.dispatch("selectOrders", this.loginUser.id)
     .then(() => {
                let order = store.getters.getOrders;
                let preOId = -1;

                for(let p of order){                    
                    p.order_time = (moment(p.order_time).format('YYYY-MM-DD HH:mm:ss'));
                    let oid = p.o_id;
                    if(preOId !== oid){
                      this.orders.push(p);
                      preOId = oid;
                    }
                    
                }
            })
  },
  computed: {
    loginUser() {
      return store.getters.getLoginUser;
    },
  },
  methods: {
    openPage(idx) {
        this.show = !this.show;
        store.dispatch("selectOrderDetails", this.orders[idx].o_id);
        
        this.orderedProduct = store.getters.getOrderDetails;
    },
    getTotalCost() {
      let sum = 0;
      for (let i of this.orderedProduct) {
        sum += i.unitprice * i.quantity;
      }
      return sum;
    },
    getStamps() {
      let sum = 0;
      for (let i of this.orderedProduct) {
        sum += i.quantity;
      }
      return sum;
    },
  },

};
</script>