<template>
  <header>
     <img :src="require('./logo.png')" />
  </header>
</template>

<script>
export default {
  props: ['title']
}
</script>

<style scoped>
header {
  width: 100%;
  height: 5rem;
  margin-top: 2%;
  margin-bottom: 4%;
  /* background-color: #640032; */
  display: flex;
  justify-content: center;
  align-items: center;
}

header h1 {
  color: white;
  margin: 0;
}
</style>
