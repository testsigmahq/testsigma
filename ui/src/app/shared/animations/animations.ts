import {animate, state, style, transition, trigger} from '@angular/animations';

export let fade = trigger('fade', [
  state('void', style({opacity: 0})),
  transition('void => *', [animate("0.25s ease-in-out")]),
  transition('* => void', [animate(0)])
]);

export let expand = trigger('expand', [
  state('void', style({width: 0})),
  transition('void <=> *', [animate("0.25s ease-in-out")])
]);
export let collapse = trigger('collapse', [
  state('void', style({height: 0})),
  transition('void <=> *', [animate("0.25s ease-in-out")])
]);
export let simultaneousCollapse = trigger('simultaneousCollapse', [
  state('void', style({height: 0})),
  transition('* => void', [
    animate('0.35s 0s ease-in-out')
  ]),
  transition('void => *', [
    animate('0.35s 0.35s ease-in-out')
  ]),
]);
