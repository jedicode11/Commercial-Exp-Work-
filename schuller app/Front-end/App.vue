<template>
  <div>
    <the-header></the-header>
    <add-resource></add-resource>
  </div>
</template>

<script>
import TheHeader from './components/layouts/TheHeader.vue';
import AddResource from './components/res/AddResource.vue';
// import TheResources from './components/learning-resources/TheResources.vue';

export default {
  components: {
    TheHeader,
    AddResource,
    // TheResources
  },
  mounted(){
    
  }
};
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap');

* {
  box-sizing: border-box;
}

html {
  font-family: 'Roboto', sans-serif;
}

body {
  margin: 0;
}

table {
  font-size: 1.2em;
  margin: 1rem auto;
  max-width: 90rem; 
}

.red_text {
  color:red;
  text-align: right;
  padding-right: 20px;
}
</style>
<!-- !! -->