import {Component, Input, OnInit} from '@angular/core';
import {SigninComponent} from "../signin/signin.component";


@Component({
  selector: 'app-testsigma-home',
  templateUrl: './home.component.html',
  styles:[`
    .big-icon::before {
      font-size: 3.35rem;
      display: inline-block;
      font-style: normal;
      font-variant: normal;
      text-rendering: auto;
      -webkit-font-smoothing: antialiased;
      margin-right: 1rem;
    }
    .registered-message { padding: 0% 5% 0% 3%}
    .registered-message h4{ font-size: 1vw; }
    .registered-message p{ font-size: 1vw; }
  `],
  host: {'class': 'page-content-container'}
})
export class HomeComponent extends SigninComponent implements OnInit {

}
