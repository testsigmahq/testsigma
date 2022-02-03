import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {BaseComponent} from "../base.component";
import {Integrations} from "../../models/integrations.model";
import {IntegrationsService} from "../../services/integrations.service";
import {AuthenticationGuard} from "../../guards/authentication.guard";
import {NotificationsService} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {TestPlanLabType} from "../../../enums/test-plan-lab-type.enum";

@Component({
  selector: 'app-inspector-test-lab-selection',
  template: `
    <h3 [translate]="'inspector.test.lab.selection.connect.to'" style="margin-right: 10px;"></h3>
    <div class="d-flex mt-13 ts-col-120" style="align-content: space-between ">
      <mat-tab-group class="test-lab-container ts-col-100 p-10" style="width: 400px" [selectedIndex]="0">
        <mat-tab>
          <ng-template mat-tab-label>
            <div class="lab-item"
                 (click)="setMobileInspectorModel(testPlanLabType.Hybrid)">
              <span [translate]="'execution.lab_type.Hybrid'"></span>
              <i class="fa-desktop fz-18 lab-icon pt-5"></i>
            </div>
          </ng-template>
        </mat-tab>
      </mat-tab-group>
    </div>
  `,
  host: {'class': 'ts-col-100 d-flex align-items-center p-5'},
})
export class InspectorTestLabSelectionComponent extends BaseComponent implements OnInit {
  public testPlanLabType = TestPlanLabType;
  public applications: Integrations[];
  public selectedTestPlanLabType: TestPlanLabType = TestPlanLabType.TestsigmaLab;
  @Output() testPlanLabTypeChange = new EventEmitter<TestPlanLabType>();

  constructor(
    private integrationsService: IntegrationsService,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.integrationsService.findAll().subscribe(res => this.applications = res);
  }

  setMobileInspectorModel(testPlanLabType: TestPlanLabType) {
    this.selectedTestPlanLabType = testPlanLabType
    this.testPlanLabTypeChange.emit(this.selectedTestPlanLabType);
  }
}
