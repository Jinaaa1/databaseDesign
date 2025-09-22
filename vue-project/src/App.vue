<template>
 <div id="app"></div>
    <RouterView/>
</template>

<style>
#app {
  background-image:url(./assets/BackgroundPicture.jpg);
  height: 100%;
  width: 100%;
  background-size: 100% 100%;
  background-attachment: fixed;
  z-index: -2;  /* 将图层置于底层 */
  position: fixed;
  overflow: auto;
  /* filter: brightness(0.95)	 */
}
</style>
