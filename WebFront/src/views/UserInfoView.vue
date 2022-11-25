<template>
    <div id="user" class="bg-secondary">
        <h1>{{ loginUser.name }}회원님은</h1>
        <div>로그인 아이디는 {{ loginUser.id }}사용하고 있습니다.</div>
        <div>
            현재 모은 스탬프 총 {{ loginUser.stamps }}개로 
            <b-button size="sm" variant="primary">{{ getLevel(loginUser.stamps) }}</b-button>단계입니다.
        </div>
        <div>
            앞으로 
            <b-button size="sm" variant="primary">{{ getDistance(loginUser.stamps) }}</b-button>
            개만 더 모으면 다음 단계입니다.
        </div>
        
        <hr width="50%">

        <b-container>
            <hr/>
            이제까지 주문 내역은
            <hr/>
            <order-detail-view/>    
        </b-container>
    </div>
    
</template>

<script>
import OrderDetailView from '@/views/OrderDetailView.vue'

export default {
    name: 'user-info-view',
    data() {
        return {
            loginUser: {},
        }
    },
    created(){
        this.loginUser = this.$store.getters.getLoginUser;
    },
    components: {
        OrderDetailView,
    },
    methods: {
        getLevel(stamps){
            if(stamps < 50){ 
                let userStampsLevel = "씨앗";
                let levelDetail = (parseInt(stamps / 10)).toString() + "단계";                
                return userStampsLevel + levelDetail
            }
            else if(stamps < 125){ 
                let userStampsLevel = "꽃";
                let levelDetail = (parseInt((stamps - 50) / 15)).toString() + "단계";                
                return userStampsLevel + levelDetail
             }
            else if(stamps < 225){
                let userStampsLevel = "열매";
                let levelDetail = (parseInt((stamps - 125) / 20)).toString() + "단계";                
                return userStampsLevel + levelDetail

            }
            else if(stamps < 350){ 
                let userStampsLevel = "커피콩";
                let levelDetail = (parseInt((stamps - 225) / 25)).toString() + "단계";            
                return userStampsLevel + levelDetail
            }
            else { return "나무" }
        },
        getDistance(stamps){
            if(stamps < 50){
                let levelAlarm = (10 - (stamps % 10)).toString();
                return levelAlarm;
             }
            else if(stamps < 125){
                 let levelAlarm = (15 - (stamps % 15)).toString();
                 return levelAlarm;
                 }
            else if(stamps < 225){ 
                let levelAlarm = (20 - (stamps % 20)).toString();
                return levelAlarm; 
                }
            else if(stamps < 25){ 
                let levelAlarm = (25 - (stamps % 25)).toString();
                return levelAlarm; 
                }
            else { return "(모을 스탬프 없이 최종 단계입니다)" }
        },
    },
}
</script>

<style scope="scope">
    #user {
        text-align: center;
        background: rgb(252, 252, 252);
        color: rgb(20, 201, 171);
    }
</style>

