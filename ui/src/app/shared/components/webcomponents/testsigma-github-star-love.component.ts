import {Component, Inject, OnInit} from "@angular/core";
import {MAT_DIALOG_DATA, MatDialog} from "@angular/material/dialog";
import {NotificationsService} from "angular2-notifications";
import {TranslateService} from "@ngx-translate/core";
import {ToastrService} from "ngx-toastr";
import {AuthenticationGuard} from "../../guards/authentication.guard";
import {BaseComponent} from "../base.component";
import {UserPreference} from "../../../models/user-preference.model";
import {UserPreferenceService} from "../../../services/user-preference.service";

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
    public matDialog: MatDialog,
    public toastrService: ToastrService,
    public userPreferenceService: UserPreferenceService,
    @Inject(MAT_DIALOG_DATA) public modalData: { showTwitter? : boolean, userPreference: UserPreference}
  ) {
    super(authGuard, notificationsService, translate, toastrService)
    this.userPreference = this.modalData.userPreference;
  }

  private userPreference: UserPreference;

  openGithub() {
    window.open('https://github.com/testsigmahq/testsigma', '_blank');
  }

  openTwitter() {
    window.open('https://twitter.com/testsigmainc', '_blank');
  }

  showTwitterCTA(){
    return (this.modalData != undefined ? this.modalData.showTwitter : true)
  }

  star() {
    this.userPreference.showedGitHubStar = true;
    this.userPreferenceService.save(this.userPreference).subscribe();
  }

  skip() {
    this.userPreference.clickedSkipForNow+=1;
    this.userPreferenceService.save(this.userPreference).subscribe();
    this.matDialog.openDialogs.find( dialog => dialog.componentInstance instanceof TestsigmaGitHubStarLoveComponent)?.close()

  }
}
