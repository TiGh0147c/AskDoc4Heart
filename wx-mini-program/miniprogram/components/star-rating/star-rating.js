// components/star-rating/star-rating.js
Component({
  properties: {
    rating: {
      type: Number,
      value: 0,
      observer: 'setStars'
    }
  },
  
  data: {
    stars: [],
    startX: 0, // 触摸起始X坐标
    startY: 0, // 触摸起始Y坐标
    currentIndex: -1 // 当前被点击的星星索引
  },

  methods: {
    setStars(newRating) {
      let newStars = [];
      for (let i = 0; i < 5; i++) {
        if (i * 2 + 1 < Math.floor(newRating)) {
          newStars.push({ filled: true, icon: '/images/icons/星星.png' });
        } else if (i * 2 + 1 === Math.floor(newRating)) {
          newStars.push({ filled: true, icon: '/images/icons/半星.png' });
        } else {
          newStars.push({ filled: false, icon: '/images/icons/空星.png' });
        }
      }
      this.setData({ stars: newStars });
    },

    onTouchStart(e) {
      this.setData({
        startX: e.touches[0].pageX,
        startY: e.touches[0].pageY
      });
    },

    onTouchMove(e) {
      // 可选：如果需要滑动评分，可以在这里处理
    },

    onTouchEnd(e) {
      const index = e.currentTarget.dataset.index;
      const endX = e.changedTouches[0].pageX;
      const endY = e.changedTouches[0].pageY;

      const query = wx.createSelectorQuery().in(this);
      query.selectAll('.star-container').boundingClientRect((rects) => {
        const centerX = rects[index].left + rects[index].width / 2;
        const centerY = rects[index].top + rects[index].height / 2;

        const deltaX = endX - centerX;
        const deltaY = endY - centerY;

        // 如果点击位置在右侧，则设置为全星，否则为半星
        const newRating = deltaX > 0 ? (index * 2 + 2) : (index * 2 + 1);

        this.triggerEvent('ratingchange', { rating: newRating });
      }).exec();

    },

    setRating(e) {
      const index = e.currentTarget.dataset.index;
      // 这里可以简单地触发触摸结束事件来处理点击
      this.onTouchEnd(e);
    }
  },
  
});