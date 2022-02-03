import {Component, Input, OnInit} from '@angular/core';
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {TestCaseService} from "../../services/test-case.service";
import {ByTypeCount} from "../../models/by-type-count.model";
import {TestCaseTypesService} from "../../services/test-case-types.service";

@Component({
  selector: 'app-test-case-by-type-summary',
  templateUrl: './test-case-by-type-summary.component.html',
  styles: []
})
export class TestCaseByTypeSummaryComponent implements OnInit {
  @Input('version') version: WorkspaceVersion;
  public entity: any;
  private defaultName = {
    "Unit Test": "#F86920",
    "Integration": "#7A68BC",
    "Functional": "#F0B14C",
    "Non Functional": "#1FB47E",
    "User Experience": "#3C8FE2"
  };
  private customColors = ["#885151", "#4a196d", "#9aca72", "#48db37", "#8cbcdd", "#1e31d6", "#d4c4e0", "#670381", "#e5576d", "#56472f"];

  constructor(
    private testCaseTypesService: TestCaseTypesService,
    private testCaseService: TestCaseService) {
  }

  ngOnInit(): void {
    this.testCaseService.byTypeCount(this.version.id).subscribe((res: ByTypeCount[]) => {
      let typeIds = res.map((byTypeCount: ByTypeCount) => byTypeCount.type);
      if (!typeIds.length) return;
      this.testCaseTypesService.findAll("id@" + typeIds.join("#")).subscribe(types => {
        this.entity = [];
        types.content.forEach(testCaseType => {
          let type = {};
          type['name'] = testCaseType.name;
          type['color'] = this.defaultName[testCaseType.name] ? this.defaultName[testCaseType.name] : this.customColors[this.getRandomNumber(10)];
          res.forEach((byTypeCount: ByTypeCount) => {
            if (byTypeCount.type == testCaseType.id)
              type['y'] = byTypeCount.count
          })
          this.entity.push(...[type])
        });
        res.forEach((byTypeCount: ByTypeCount) => {
          if (!byTypeCount.type) {
            let type = {};
            type['name'] = "Not Yet Mapped";
            type['color'] = '#dcdcdc';
            type['y'] = byTypeCount.count;
            this.entity.push(...[type]);
          }
        })
      });
    });
  }

  getRandomNumber(max) {
    return Math.floor(Math.random() * Math.floor(max));
  }

}
