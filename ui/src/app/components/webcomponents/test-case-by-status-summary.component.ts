import {Component, Input, OnInit} from '@angular/core';
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {TestCaseService} from "../../services/test-case.service";
import {ByStatusCount} from "../../models/by-status-count.model";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-test-case-by-status-summary',
  templateUrl: './test-case-by-status-summary.component.html',
  styles: []
})
export class TestCaseByStatusSummaryComponent implements OnInit {
  @Input('version') version: WorkspaceVersion;
  public color = {
    READY: '#1FB47E',
    UPDATE: '#F0B14C',
    OBSOLETE: '#F86920',
    IN_REVIEW: '#3C8FE2',
    DRAFT: '#C4C4C4',
    REWORK: '#d80a00'
  };
  public entity: any;

  constructor(
    private testCaseService: TestCaseService,
    private translate: TranslateService) {
  }

  ngOnInit(): void {
    this.testCaseService.byStatusCount(this.version.id).subscribe((res: ByStatusCount[]) => {
      this.entity = [];
      res.forEach(item => {
        let statusItem = {};
        this.translate.get('testcase.status_' + item.status).subscribe(res => {
          statusItem['name'] = res;
        });
        statusItem['y'] = item.count
        statusItem['color'] = this.color[item.status]
        this.entity.push(...[statusItem])
      })
    });
  }

}
