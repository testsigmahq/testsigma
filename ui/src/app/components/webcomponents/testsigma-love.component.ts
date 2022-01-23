import {Component, OnInit} from "@angular/core";
import {BaseComponent} from "../../shared/components/base.component";

@Component({
  selector: 'app-testsigma-love',
  templateUrl: './testsigma-love.component.html',
  styles: []
})
export class TestsigmaLoveComponent extends BaseComponent implements OnInit {

  openGithub() {
    window.open('https://github.com/testsigmahq/testsigma', '_blank');
  }

  openTwitter() {
    window.open('https://twitter.com/testsigmainc', '_blank');
  }
}
