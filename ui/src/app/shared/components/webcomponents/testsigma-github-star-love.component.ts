import {Component, Inject, OnInit} from "@angular/core";
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {NotificationsService} from "angular2-notifications";
import {TranslateService} from "@ngx-translate/core";
import {ToastrService} from "ngx-toastr";
import {AuthenticationGuard} from "../../guards/authentication.guard";
import {BaseComponent} from "../base.component";

@Component({
  selector: 'app-testsigma-love',
  templateUrl: './testsigma-github-star-love.component.html',
  styles: []
})
export class TestsigmaGitHubStarLoveComponent extends BaseComponent implements OnInit {

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    @Inject(MAT_DIALOG_DATA) public modalData: { showTwitter? : boolean}
  ) {
    super(authGuard, notificationsService, translate, toastrService)
  }


  openGithub() {
    window.open('https://github.com/testsigmahq/testsigma', '_blank');
  }

  openTwitter() {
    window.open('https://twitter.com/testsigmainc', '_blank');
  }

  showTwitterCTA(){
    return (this.modalData != undefined ? this.modalData.showTwitter : true)
  }
}
