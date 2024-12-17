<template>
  <button :class="mode" class="animated">
    <slot></slot>
  </button>
</template>

<script>
export default {
  props: ['mode']
}
</script>

<style scoped>

button {
  padding: 0.1rem 1.5rem;
  font-family: inherit;
  font-size: 3.6em;
  background-color: #f50013;
  border: 2px solid #000000;
  border-radius: 9px;
  color: white;
  cursor: pointer;
  width: 100%;
  
}

button:hover{
    /* При задържане ще го повдигне на горе малко */
    transform: translateY(-3px);
    /* За сянка на бутонът */
    box-shadow: 0 10px 20px rgb(0, 0, 0,.2);
}
button:active {
  transform: translateY(-1px);
  /* За сянка на бутонът */
  box-shadow: 0 5px 10px rgb(0, 0, 0,.2);
}
button:visited{
  text-transform: uppercase;
 text-decoration: none;
 padding: 15px 40px;
 display: inline-block;
 border: 0.5px double #fca55e;
 /* Да се заоблят ъглите на бутонът */
 border-radius: 9px;
/* Всички transition да се пускат 2 с */
transition: all .2s;

/* Това ще ми трябва за .btn::after */
position: relative;
}
.flat {
  background-color: transparent;
  color: #3a0061;
  border: none;
}

.flat:hover,
.flat:active {
  background-color: #edd2ff;
}



.animated {
  animation: moveInBottom .7s ease-out .75s;
  /* Преди анимацията да почне да се пусене moveInBottom */
  animation-fill-mode: backwards;
}
@keyframes moveInBottom {
  0% {
      opacity: 0;
   /*Ако е положително ще направи изместване 
    от дясно на ляво */
      transform:translateY(30px);
  }

  100%{
      opacity: 1;
   /*Ресет към първоначалната му позиция */
      transform: translate(0);
  }
}
</style>