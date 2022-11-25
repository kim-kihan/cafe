 <template>
    <b-container>
        <h2>상품 평점</h2>

        <b-card 
            bg-variant="info"
            :img-src="require('@/assets/menu/' + product.img)" 
            img-left img-height="400px" 
            img-width="400px">
            <table>
                <tr>상품명 : {{ product.name }}</tr>
                <tr>상품단가 : {{ product.price }} </tr>
                <tr>총주문수량 : {{ product.cnt }} </tr>
                <tr>평가 수 : {{ product.rateCnt }} </tr>
                <tr>평균평점 : {{ product.rating }}</tr>
            </table>
            <div>
                <b-button v-b-modal.regist id="review" variant="light" v-show="loginUser.id">한줄평 남기기</b-button>
                <b-modal id="regist" title="한줄평 남기기" @ok="registComment" @show="resetModal" @hidden="resetModal">
                     <form>
                        <p>평점</p>
                        <input type="number" min="0" max="10" v-model="score" required>
                        <div>한줄 평 : </div>
                        <input type="text" v-model="text" required>
                    </form>
                </b-modal>
            </div>
        </b-card>
       
        <hr>
        <div>자신이 남긴 평가만 수정, 삭제할 수 있습니다.</div>
        
        <hr>
        <div>
            <b-table-simple>
                <tr>
                    <th>사용자</th>
                    <th>평점</th>
                    <th>한줄평</th>
                </tr>

                <tr v-for="(item, idx) in comments" :key="idx">                
                    <td>{{ item.userId }}
                    </td>
                    <td>{{ item.rating }}</td>
                    <td>
                        {{ item.comment }}
                        <span v-show="checkUser(item.userId)">
                            <b-button size="sm" variant="success" v-b-modal.update>수정</b-button>
                            <b-modal v-if="checkUser(item.userId)" id="update" title="한줄평 수정하기" @ok="updateComment(item)" @show="resetModal" @hidden="resetModal">
                                <form>
                                    <p>평점</p>
                                    <input id="score" type="number" min="0" max="10" :value="item.rating" required>
                                    <div>한줄 평 : </div>
                                    <input id="text" type="text" :value="item.comment" required>
                                </form>
                            </b-modal>
                            <b-button size="sm" @click="deleteComment(item.id, item.comment)">삭제</b-button>
                        </span>
                    </td>
                </tr>
            </b-table-simple>
        </div>

     </b-container>
</template>

<style scope="scope">
h2 {
    text-align: center;
}
table {
    color: white;
    width: 100%;
    height: 80%;
    border: 1px solid white;
    border-collapse: collapse;
    text-align: center;
}
tr {
    padding: 10px; 
    border: 1px solid white;
}
#review {
    margin-top: 30px;
    margin-left: 530px;
}
</style>

<script>
export default {
    name: 'product-detail-view',
    data(){
        return {
            idx: this.$route.query.idx,
            product: [],
            comments : [],
            score: 0,
            text: '',
            modal: true,
        }
    },
    created() {
        let allProduct = this.$store.getters.getProducts;
        let idx = this.$route.query.idx;
        this.product = allProduct[idx];
        console.log(this.product)
        this.$store.dispatch('selectComments', this.product.id)
        .then(() => {
            this.getComments();
        })
        
    },
    methods: {
        getComments(){
            this.comments = this.$store.getters.getComments;
            this.product.rateCnt = this.comments.length; 

            let sum = 0;
            for(let c of this.comments){
                sum += parseInt(c.rating);
            }
            let avg = (sum / this.product.rateCnt).toFixed(2); 
            this.product.rating = avg;
        },
        checkUser(user_id){
            if(this.loginUser.id === user_id) return true;
            else return false;
        },
        resetModal(){
            this.score = 0;
            this.text = '';
        },
        registComment(e){
            e.preventDefault();

             for(let c of this.comments){
                if(c.userId == this.loginUser.id){
                    alert('이미 상품평을 등록 하였습니다.');
                    return;
                }
            }

            this.$store.dispatch('registComment', {
                id : '',
                userId: this.loginUser.id,
                productId: this.product.id,
                rating: this.score,
                comment: this.text,
            })
            .then(() => {
                 this.getComments();
                  this.$store.dispatch('selectComments', this.product.id)
                    .then(() => {
                        this.getComments();
                    })
            });      

            this.$nextTick(() => {
                this.$bvModal.hide('regist');
            })

        },
        updateComment(item){
            item.rating = document.getElementById("score").value;
            item.comment = document.getElementById("text").value;
            
            this.$store.dispatch('updateComment', item)
            .then(() => {
                this.getComments();
            })

            this.$nextTick(() => {
                this.$bvModal.hide('regist');
            })
        },
        deleteComment(id, comment){
            if(!confirm(comment + "를 삭제하시겠습니까?")){
                console.log(id);
            } else {            
                this.$store.dispatch('deleteComment', id)
                .then(() => {
                    this.getComments();
                     this.$store.dispatch('selectComments', this.product.id)
                    .then(() => {
                        this.getComments();
                    })
                })
            }
        },
    },
    computed: {
        loginUser() {
            return this.$store.getters.getLoginUser;
        }
    },
}
</script>
