<template>
  <div class="container">
    <div class="form-con">
      <img src="https://image.freepik.com/free-vector/secure-data-concept-illustration_114360-483.jpg">
      <p class="p-dark">Login</p>
      <br/>
      <form>

        <div class="input-con">
          <i class="fas fa-user icon"></i>
          <input v-model="user.username" type="text" placeholder="Username">
        </div>

        <div class="input-con">
          <i class="fas fa-lock icon"></i>
          <input v-model="user.password" type="password" placeholder="Password">
        </div>

        <button type="button" @click="login"><p>Login</p></button>
      </form>

    </div>
  </div>
</template>

<script>

import {useRouter} from 'vue-router'

export default {
  name: "LoginView",

  setup() {

    const router = useRouter()

    const user = {
      username: "admin",
      password: "admin"
    }

    const login = () =>{
      if(user.username === "admin" && user.password === "admin"){
        router.push('hub')
      }
    }

    return {
      user,
      login
    }
  }
}

</script>

<style scoped>

/*容器div*/
.container {
  font-family: 'Roboto', sans-serif;
  height: 100%;
  width: 100%;
  top: 0;
  left: 0;
}

/*内容div*/
.form-con {
  width: 400px;
  height: 500px;
  margin: 50px auto;
  position: relative;
  background-color: #ffffff;
  border-radius: 25px;
  overflow: hidden;
}

.form-con form {
  display: flex;
  flex-wrap: wrap;
  padding: 0 0 100px 0;
}

.form-con img {
  display: block;
  width: 80%;
  margin-top: -50px;
  margin-left: auto;
  margin-right: auto;
}

.form-con .p-dark, .p-light {
  font-size: 16px;
  text-align: center;
  height: 30px;
}

.form-con .p-dark {
  font-weight: 700;
  color: #515151;
  font-size: 24px;
  margin: -20px 0 0 0;
}

.form-con .p-light {
  font-weight: 100;
  color: #c5c5c5;
  margin: 0 0 30px 0;
}

.form-con form .input-con {
  width: 80%;
  margin: auto;
}

.form-con form .input-con .icon {
  position: relative;
  top: 32px;
  left: 20px;
  color: #ff4f5a;
}

.form-con form .input-con input {
  width: 100%;
  height: 45px;
  margin: 0px auto;
  border-radius: 25px;
  outline: none;
  border: 2px solid #eeeeee;
  font-size: 18px;
  padding: 0 40px;
  box-sizing: border-box;
}

.form-con form button {
  background-color: #1b2e35;
  border: 0;
  width: 80%;
  height: 50px;
  margin: 25px auto;
  border-radius: 25px;
  outline: none;
  cursor: pointer;
}

.form-con form button p {
  color: #ffffff;
  font-size: 16px;
  font-weight: 700;
}

.form-con form input::placeholder {
  color: #c7c7c7;
  font-weight: 100;
  font-size: 16px;
  font-family: 'Roboto', sans-serif;
}
</style>