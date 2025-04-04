// components/modal-waiting/modal-waiting.js
Component({
  properties: {
    showModal: {
      type: Boolean,
      value: false,
    },
    modalData: {
      type: Object,
      value: null,
    },
  },

  data: {
    animationData: {}, // 动画数据
  },
  
  lifetimes: {
    attached() {
      // 创建动画实例
      this.animation = wx.createAnimation({
        duration: 300, // 动画持续时间（毫秒）
        timingFunction: 'ease-in-out', // 缓动函数
      });
    },
  },

  methods: {
    stopPropagation() {},

    // 打开模态框动画
    animateUp() {
      this.animation.translateY(0).step(); // 从底部滑入
      this.setData({ animationData: this.animation.export() });
    },

    // 关闭模态框动画
    animateDown() {
      this.animation.translateY('100%').step(); // 滑出到屏幕底部
      this.setData({ animationData: this.animation.export() });
    },

    // 显示模态框
    show() {
      this.animateUp(); // 触发滑入动画
    },

    // 隐藏模态框
    hide() {
      this.animateDown(); // 触发滑出动画
      setTimeout(() => {
        this.triggerEvent('close'); // 延迟触发关闭事件
      }, 300); // 等待动画完成后再关闭
    },

    // 关闭模态框
    handleClose() {
      this.hide();
    },
  },
});