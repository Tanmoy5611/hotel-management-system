import { animate } from 'animejs';

export function showAnimatedToast(toastElement, Toast, options = {}) {
  // The helper accepts Bootstrap Toast so UI modules do not repeat setup code
  if (!toastElement) {
    return;
  }

  // AnimeJS gives the toast a small movement instead of appearing suddenly
  animate(toastElement, {
    opacity: [0, 1],
    translateY: [12, 0],
    duration: 350,
    easing: 'easeOutQuad',
  });

  const toast = new Toast(toastElement, {
    delay: options.delay ?? 3000,
  });

  toast.show();
}