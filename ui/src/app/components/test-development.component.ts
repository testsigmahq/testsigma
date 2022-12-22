import {Component, Inject, OnInit, Optional} from '@angular/core';
import {WorkspaceVersionService} from "../shared/services/workspace-version.service";
import {BaseComponent} from "../shared/components/base.component";
import {ActivatedRoute, Router} from '@angular/router';
import {WorkspaceVersion} from "../models/workspace-version.model";
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {NavigationService} from "../services/navigation.service";

@Component({
  selector: 'app-test-development',
  templateUrl: './test-development.component.html',
  styles: []
})
export class TestDevelopmentComponent extends BaseComponent implements OnInit {
  public versionId: Number;
  public version: WorkspaceVersion;

  constructor(
    public route: ActivatedRoute,
    public router: Router,
    private versionService: WorkspaceVersionService,
    private navigation:NavigationService,
    @Optional() private dialogRef?: MatDialogRef<TestDevelopmentComponent>,
    @Optional() @Inject(MAT_DIALOG_DATA) public data?: {versionId: number}) {
    super();
  }

  ngOnInit() {
    this.route.params.subscribe((params) => {
      this.pushToParent(this.route, params);
      this.versionId = params.versionId | this.data?.versionId;
      if (this.versionId > 0)
        this.fetchVersion()
    });
  }

  get detailsAliasName(){
    let leftPath = this.router.url.split("/")[3]
    if(isNaN(parseInt(leftPath)))
      return  leftPath
    else
      return this.router.url.split("/")[2];
  }

  fetchVersion() {
    this.versionService.show(this.versionId).subscribe(res => this.version = res, err => {
      if (err.status == 404) {
        this.versionService.findAll("isDemo:true").subscribe(versions => {
          this.version = versions.content[0];
          this.navigation.replaceUrl(['/td', this.version.id, 'cases'])
        });
      };
    });
  }

  closeDialog(){
    if(this.dialogRef)
      this.dialogRef.close();
  }
}
