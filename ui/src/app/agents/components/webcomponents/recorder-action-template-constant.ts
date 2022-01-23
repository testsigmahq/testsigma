import { Platform } from "app/enums/platform.enum";

export class RecorderActionTemplateConstant {
  static launch_app: templateIds =
    {[Platform.Android]: 20001 ,[Platform.iOS]: 30001};
  static tap: templateIds =
    {[Platform.Android]: 20073 ,[Platform.iOS]: 30073};
  static tap_on_coordinates: templateIds =
    {[Platform.Android]: 20091 ,[Platform.iOS]: 30090};
  static enter: templateIds =
    {[Platform.Android]: 20016 ,[Platform.iOS]: 30016};
  static clear: templateIds =
    {[Platform.Android]: 20076 ,[Platform.iOS]: 30076};
  static navigateBack: templateIds =
    {[Platform.Android]: 20090 ,[Platform.iOS]: 30089};
  static setOrientationAsPortrait: templateIds =
    {[Platform.Android]: 20005 ,[Platform.iOS]: 30005};
  static setOrientationAsLandscape: templateIds =
    {[Platform.Android]: 20006 ,[Platform.iOS]: 30006};
  static tapByRelativeCoordinates: templateIds =
    {[Platform.Android]: 20139 ,[Platform.iOS]: 30128};
  static switchToWebviewContext: templateIds =
    {[Platform.Android]: 20130 ,[Platform.iOS]: 30137};
  static switchToNativeAppContext: templateIds =
    {[Platform.Android]: 20131 ,[Platform.iOS]: 30138};
  static swipeByRelativeCoordinates: templateIds =
    {[Platform.Android]: 20150 ,[Platform.iOS]: 30144};
}
interface templateIds {[Platform.Android]: number  ,[Platform.iOS]: number};
