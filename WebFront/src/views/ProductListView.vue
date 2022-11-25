<template>
    <div>
        <hr width="90%">
        <div id="msg">
            <h1>커피</h1>
            <div>뛰면서 즐기는 커피 한잔의 여유</div>
            <hr width="90%">
            <div>갓 볶은 아라바키산 원두만 고집합니다.</div>
            <hr width="50%">
        </div>

        <b-row id="main">
            <b-col cols="3" v-for="(item, idx) in products" :key="idx" class="mt-4">
                <b-card
                    :img-src="require('@/assets/menu/' + item.img)"
                    img-top="img-top"
                    img-height="200px">
                    <b-card-body>
                        <b-card-title  @click="movePage('/product-detail-view?idx='+ idx)">{{ item.name }}</b-card-title>
                        <b-card-text>{{ item.price }}원</b-card-text>

                        <div v-show="loginUser.id">
                            <b-button @click="removeList(item, idx)" id="lbtn" size="sm">-</b-button>
                            <label>{{ item.cnt }}</label>
                            <b-button @click="addList(item, idx)" id="rbtn" size="sm" variant="primary">+</b-button>
                        </div>
                    </b-card-body>
                </b-card>
            </b-col>
        </b-row>

        <div id="order" v-show="loginUser.id">
            <label>주문서</label>
            <b-list-group  v-for="(item, idx) in temp_products" :key="idx">
                <b-list-group-item>
                    <b-img :src="require('@/assets/menu/' + item.img)" width="40px" img-height="30px"></b-img> 
                    <span>   {{ item.name }}  </span> 
                    <b-button variant="secondary" size="sm">{{ item.cnt }}</b-button>
                </b-list-group-item>
            </b-list-group>
            <b-button id="ol" variant="primary" @click="orders(temp_products)">주문하기</b-button>
        </div>

    </div>
</template>

<script>
    import store from "@/store"
    export default {
        name: 'product-list-view',
        data(){
            return {
                products: [],
                order: {
                completed: 'N',
                details: [{
                orderId: 0,
                productId: '',
                quantity: ''
                }],
                stamps: [{
                userId: '',
                orderId: '',                
                quantity: '', 
                }],
                user: {              
                    id: '',            
                    stamps: ''
                },
                form: {
                    id: '',
                    pass: ''
                },
                orderTable: 'orderTable',
                userId: ''
                },
                

            }
        },
        created() {
            this.$store.dispatch('selectProducts')
            .then(() => {
                let product = store.getters.getProducts;
                for(let p of product){
                    p.cnt = 0;
                    this.products.push(p);
                }
            })
            localStorage.removeItem('temp_products');
        },
        methods: {
            movePage(url){
                this.$router.push(url);
            },
            addList(product, idx) {
                this.products[idx].cnt++;
                this.$store.dispatch('addTempProducts', product);
            },
            removeList(product, idx) {
                if(this.products[idx].cnt === 0){
                    alert("담은 수량이 0개 입니다.")
                }
                else {
                    this.products[idx].cnt--;
                    this.$store.dispatch('removeTempProducts', product); 
                }
            },
            orders(products) {     
                if(products == ""){
                    alert("장바구니가 비어있습니다.")
                } else{
                    this.order.userId = this.loginUser.id;
                    let count = 0;

                    for(let p of products){
                        this.order.details[count++] = {
                            orderId: 1,
                            productId: p.id,
                            quantity: p.cnt
                        }
                    }
                    this.order.stamps = {
                        userId: this.loginUser.id,
                        orderId: '',                
                        quantity: '', 
                    }

                    this.order.form.id = this.loginUser.id
                    this.order.form.pass = this.loginUser.pass

                    this.$store.dispatch('makeOrderDetail', this.order)
            
                }
            }
        },
        computed: {
            loginUser() {
                return this.$store.getters.getLoginUser;
            },
            temp_products(){
                return this.$store.getters.getTempProducts;
            },
        }
    }
</script>

<style scope="scope">
    #rbtn {
        width: 30px;
        height: 30px;
        float: right;
        margin-right: 10px;
    }
    #lbtn {
        width: 30px;
        height: 30px;
        float: right;
    }
    #main {
        width: 800px;
        position: absolute;
        left: 50%;
        transform: translateX(-60%);
    }
    #msg {
        text-align: center;
        background: rgb(252, 252, 252);
    }
    #order {
        position: absolute;
        left: 75%;
        transform: translateX(0%);
        width: 250px;
        text-align: center;
        background: rgb(214, 210, 245);
    }
    #orderlist {
        width: 150px;
    }
</style>
