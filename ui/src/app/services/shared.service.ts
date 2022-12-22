import {Injectable} from "@angular/core";
import {LinkedEntitiesModalComponent} from "../shared/components/webcomponents/linked-entities-modal.component";
import {TranslateService} from "@ngx-translate/core";
import {MatDialog} from "@angular/material/dialog";
import {InfiniteScrollableDataSource} from "../data-sources/infinite-scrollable-data-source";
import {TestStep} from "../models/test-step.model";
import {TestCase} from "../models/test-case.model";

@Injectable({
  providedIn: 'root'
})
export class SharedService{
  constructor(
    private translate:TranslateService,
    private matModal: MatDialog,
  ) {
  }

  public openLinkedTestStepsDialog(testSteps:TestStep[],preRequisiteTestSteps:TestStep[],bulkDelete?:Boolean) {
    let steps:TestStep[] = [];
    preRequisiteTestSteps?.map((prerequisiteTestStep)=>{
      testSteps.map(teststep=>{
        let tempTestStep = new TestStep().deserialize(teststep.serialize());
        if(teststep.id===prerequisiteTestStep.id){
          if(prerequisiteTestStep.preRequisiteStepId==null){
            tempTestStep.preRequisiteStepId = null;
          }
          if(teststep.stepGroup?.name){
            tempTestStep.stepGroup = new TestCase();
            tempTestStep.stepGroup.name = teststep.stepGroup.name;
          }
          tempTestStep.stepDisplayNumber = teststep.stepDisplayNumber;
          steps.push(tempTestStep);
        }
      })
    })
    let list = new InfiniteScrollableDataSource();
    list.cachedItems=list.cachedItems.concat(steps);
    list.dataStream.next(list.cachedItems);
    this.translate.get("step_is_prerequisite_to_another_step").subscribe((res) => {
      this.matModal.open(LinkedEntitiesModalComponent, {
        width: '568px',
        height: 'auto',
        data: {
          description: res,
          linkedEntityList: list,
          bulkDelete: bulkDelete,
        },
        panelClass: ['mat-dialog', 'rds-none']
      });
    });
  }
}
