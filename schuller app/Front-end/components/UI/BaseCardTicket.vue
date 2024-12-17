<template>
    <div class="ticket">
      <div class="ticket-content">
        <div class="ticket-text">
            <slot></slot>
        </div>
      </div>
    </div>
  </template>
  
  <style scoped>
  .ticket {
    position: relative;
    box-sizing: border-box;
    width: 300px;
    height: 451px;
    margin: 13px auto 0;
    padding: 16px;
    border-radius: 10px;
    background: #FBFBFB;
    box-shadow: 2px 2px 15px 0px #9f9f9d;
  }
  
  .ticket:before,
  .ticket:after {
    content: '';
    position: absolute;
    left: 5px;
    height: 6px;
    width: 290px;
  }
  
  .ticket:before {
    top: -5px;
    background: radial-gradient(circle, transparent, transparent 50%, #FBFBFB 50%, #FBFBFB 100% ) -7px -8px / 16px 16px repeat-x;
  }
  
  .ticket:after {
    bottom: -5px;
    background: radial-gradient(circle, transparent, transparent 50%, #FBFBFB 50%, #FBFBFB 100% ) -7px -2px / 16px 16px repeat-x;
  }
  
  .ticket-content {
    box-sizing: border-box;
    height: 100%;
    width: 100%;
    border: 3px solid #D8D8D8;
  }
  
  .ticket-text {
    font-family: 'Helvetica', 'Arial', sans-serif;
    font-size: 0.9rem;
    /* font-weight: 200; */
    padding-top: 1.5rem;
    width: 100%;
    text-transform: uppercase;
    color: #439191;
  }
  </style>
  