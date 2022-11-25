<template>
    <div class="main">
        <b-navbar toggleable="lg" type="dark" variant="info">
            <b-navbar-brand @click="movePage('/')" style="cursor:pointer">
                <img id="logo" src="@/assets/logo.png">
                    SSAFY Cafe
                </b-navbar-brand>

                <b-navbar-nav class="ml-auto" v-show="!loginUser.id">
                    <b-button size="sm" type="button" variant="dark" @click="movePage('/login')">로그인</b-button>
                    <b-button size="sm" type="button" variant="dark" @click="movePage('/register')">회원가입</b-button>
                </b-navbar-nav>

                 <b-navbar-nav class="ml-auto" v-show="loginUser.id">
                    <b-button size="sm" type="button" variant="dark" @click="movePage('/user-info')">{{ loginUser.id }}</b-button>
                    <b-button size="sm" type="button" variant="dark" @click="logout">로그아웃</b-button>
                </b-navbar-nav>
        </b-navbar>
    </div>
</template>

<script>    
export default {
    name: 'header-nav',
    methods: {
        movePage(url){
            this.$router.push(url);
        },
        logout() {            
            this.$router.go()
            alert("로그아웃 되었습니다.");
            
        }
    },
    computed: {
        loginUser() {
            return this.$store.getters.getLoginUser;
        }
    }
}
</script>


<style scope="scope">
    #logo {
        width: 40px;
        height: 40px;
    }
</style>

