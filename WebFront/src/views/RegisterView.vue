<template>
    <b-container>
        <h1>회원가입</h1>
        <b-form>
           <b-form-group :label="'아이디:'" label-for="input-1">                    
            <b-form-input id="input-1" v-model="form.id" type="text" required></b-form-input>
            <b-button @click="check">중복체크</b-button>
            <span class="error" v-show="isNotRegister">다른 아이디를 사용해주세요</span>
            <span class="noerror" v-show="isRegister">사용 가능한 아이디입니다</span>
            
          </b-form-group>

            <b-form-group :label="'이름:'" label-for="input-2">
                <b-form-input id="input-2" v-model="form.name" type="text" required></b-form-input>
            </b-form-group>

            <b-form-group :label="'비밀번호:'" label-for="input-3">
                <b-form-input id="input-3" v-model="form.pass" type="password" required></b-form-input>
            </b-form-group>

            <b-form-group :label="'생년월일:'" label-for="input-4">
                <b-form-input id="input-4" type="date" v-model="form.age" required></b-form-input>
            </b-form-group>

            <b-form-group :label="'성별:'" label-for="input-5">
                    <select v-model="form.gender">  
                        <option>Man</option>
                        <option>Woman</option>
                    </select>
            </b-form-group>
        </b-form>

        <div>
            <b-button variant="primary" @click="register">가입</b-button>
            <b-button variant="danger" @click="reset">취소</b-button>
        </div>
    </b-container>
</template>

<script>
import store from "@/store"

export default {
    name: 'register-view',
    data() {
        return {
            form: {
                id: '',
                name: '',
                pass: '',
                stamps: 0,
                age: '',
                gender: '',            
            },
            isNotRegister: false,
            isRegister: false
        }
    },
    methods: {
        register() {
            if(!this.form.id) {
                alert('아이디를 입력하세요.');
                return;
            }
            if(!this.form.name) {
                alert('이름을 입력하세요.');
                return;
            }
            if(!this.form.pass) {
                alert('비밀번호를 입력하세요.');
                return;
            }

            if(!this.form.age) {
                alert('생년월일을 입력하세요.');
                return;
            }
            
            if(!this.form.gender) {
                alert('성별 선택하세요.');
                return;
            }

            if(this.form.gender == "Man"){
                this.form.gender = "m"
            }

            if(this.form.gender == "Woman"){
                this.form.gender = "w"
            }

            if(this.isRegister === true){
                this.$store.dispatch('insertUser', this.form);
                this.movePage("/");
            } else{
                alert('아이디 중복체크를 해주세요');
            }


            

        },
        reset() {
            this.form.id = '';
            this.form.name = '';
            this.form.pass = '';
            this.form.age = '';
            this.form.gender = '';
            this.isNotRegister = false;
            this.isRegister = false;
        },
        movePage(url) {
            this.$router.push(url);
        },
        check(){
            if(this.form.id === ""){
                alert('아이디를 입력하세요.');
            } else{
                this.$store.dispatch('checkUser', this.form.id)
                .then(() => {
                        let check = store.getters.getBol;
                        if(check === false){
                            this.isRegister = true;
                            this.isNotRegister = false;
                        } else{
                            this.isNotRegister = true;
                            this.isRegister = false;
                        }
                 })
            }
            
        }
    }
}
</script>

<style scoped>
.error {
    color: red;
}
</style>
