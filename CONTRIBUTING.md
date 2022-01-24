# Contributing to Testsigma

Thank you for your interest in Testsigma and for taking the time to contribute to this project. If you feel insecure about how to start contributing, feel free to ask us on our [Discord Server](https://discord.gg/invite/5caWS7R6QX) in the #contributors channel.

## **Code of conduct**

Read our [Code of Conduct](CODE_OF_CONDUCT.md) before contributing.


## **How can I contribute?**

There are many ways in which you can contribute to Testsigma.

#### 👥 Join the community
&emsp;&emsp;Join our [Discord server](https://discord.gg/invite/5caWS7R6QX), help others use Testsigma for their test automation requirements.

#### 🖋️ Blog about Testsigma
&emsp;&emsp;We display blogs featuring Testsigma on our [Blogs](https://testsigma.com/blog) page. If you'd like your blog to be published here, write to us at [community@testsigma.com](mailto:community@testsigma.com).

#### 🗣️ Give a talk about Testsigma
&emsp;&emsp;You can talk about Testsigma in online/offline meetups. Drop a line to [community@testsigma.com](mailto:community@testsigma.com) ahead of time and we'll send you some swag. 👕

#### 🧩 Build an Add-on 
&emsp;&emsp;Enhance Testsigma’s automation capabilities by building add-ons to solve unique test automation problems. Check out [Contribution Guide](https://github.com/testsigmahq/testsigma-addons/blob/main/CONTRIBUTING.md) for more details.

#### 🐞 Report a bug
&emsp;&emsp;Report all issues through GitHub Issues using the [Report a Bug](https://github.com/testsigmahq/testsigma/issues/new/choose) template. Provide as much information as you can.

#### 🛠 Create a feature request
&emsp;&emsp;We welcome all feature requests, whether for new features or enhancements to existing features. File your feature request through GitHub Issues using the [Feature Request](https://github.com/testsigmahq/testsigma/issues/new/choose) template.

#### 📝 Improve the documentation
&emsp;&emsp;Suggest improvements to our documentation using the [Documentation Improvement](https://github.com/testsigmahq/testsigma-docs/issues/new/choose) template or check out our [Docs Contribution Guide](https://github.com/testsigmahq/testsigma-docs/blob/main/CONTRIBUTING.md). Testsigma docs are published on [here](https://testsigma.com/docs/)


#### 📚 Contribute to Tutorials 
&emsp;&emsp;You can help by suggesting improvements to our tutorials using the [Tutorials Improvement](https://github.com/testsigmahq/testsigma-tutorials/issues/new/choose) template or create a new tutorial. Checkout [Tutorials Contribution Guide](https://github.com/testsigmahq/testsigma-tutorials/blob/main/CONTRIBUTING.md) for more details.


#### ⚙️ Write code to fix a Bug / new Feature Request
&emsp;&emsp;We welcome contributions that help make testsigma bug-free & improve the test automation experience for our users. You can also find issues tagged [Good First Issues](https://github.com/testsigmahq/testsigma/issues?q=is%3Aopen+is%3Aissue+label%3A"good+first+issue"). Check out the below sections to begin.

&emsp;

## **Writing Code**
All submissions, including submissions by project members, require review. Before raising a pull request, ensure you have raised a corresponding issue and discussed a possible solution with a maintainer. This gives your pull request the highest chance of getting merged quickly. Join our [Discord Server](https://discord.gg/invite/5caWS7R6QX) if you need any help.

 
### First-time contributors
We appreciate first-time contributors and we are happy to assist you in getting started. In case of questions, just [reach out to us!](https://discord.gg/invite/5caWS7R6QX)
You find all issues suitable for first-time contributors [here.](https://github.com/testsigmahq/testsigma/issues?q=is%3Aopen+is%3Aissue+label%3A%22good+first+issue%22)


### Repo overview

[TestsigmaHQ/testsigma](https://github.com/testsigmahq/testsigma) is a mono-repo
consisting of 4 components:

1. Server
2. Agent
3. Automator
4. UI


For all contributions, a CLA (Contributor License Agreement) needs to be signed [here](https://cla-assistant.io/testsigmahq/testsigma) before (or after) the pull request has been submitted. A bot will prompt contributors to sign the CLA via a pull request comment, if necessary.


### Set up your branch to write code

We use [Github Flow](https://guides.github.com/introduction/flow/index.html), so all code changes happen through pull requests. [Learn more.](https://blog.scottlowe.org/2015/01/27/using-fork-branch-git-workflow/) 

 1. Please make sure there is an issue associated with the work that you're doing. If it doesn’t exist, [create an issue.](https://github.com/testsigmahq/testsigma/issues/new/choose)
 2. If you're working on an issue, please comment that you are doing so to prevent duplicate work by others also.
 3. Fork the repo and create a new branch from the `dev` branch.
 4. Please name the branch as <span style="color:grey">issue-[issue-number]-[issue-name(optional)]</span> or <span style="color:grey">feature-[feature-number]–[feature-name(optional)]</span>. For example, if you are fixing Issue #205 name your branch as <span style="color:grey">issue-205 or  issue-205-selectbox-handling-changes</span>
 5. Squash your commits and refer to the issue using `Fix #<issue-no>` in the commit message, at the start.
 6. Rebase `dev` with your branch and push your changes.
 7. Raise a pull request against the staging branch of the main repository.


### 🏡  Setup for local development

Refer to [this](https://testsigma.com/docs/contributing/setup-dev-environment/) document to learn how to set up a dev environment.

&emsp;

## **Committing code**

The repository contains two important (protected) branches.

 * main contains the code that is tested and released. 
 * dev  contains recent developments under testing. This branch is set as the default branch, and all pull requests should be made against this branch.

Pull requests should be made against the <span style="color:grey">dev</span> branch. <span style="color:grey">staging</span> contains all of the new features and fixes that are under testing and ready to go out in the next release.


#### **Commit & Create Pull Requests** 

 1. Please make sure there is an issue associated with the work that you're doing. If it doesn’t exist, [create an issue](https://github.com/testsigmahq/testsigma/issues/new/choose).
 2. Squash your commits and refer to the issue using `Fix #<issue-no>` in the commit message, at the start.
 3. Rebase `dev` with your branch and push your changes.
 4. Once you are confident in your code changes, create a pull request in your fork to the `dev` branch in the testsigmahq/testsigma base repository.
 5. Link the issue of the base repository in your Pull request description. [Guide](https://docs.github.com/en/issues/tracking-your-work-with-issues/linking-a-pull-request-to-an-issue)
 6. Fill out the [Pull Request Template](./.github/PULL_REQUEST_TEMPLATE.md) completely within the body of the PR. If you feel some areas are not relevant add `N/A` but don’t delete those sections.


####  **Commit messages**

- The first line should be a summary of the changes, not exceeding 50
  characters, followed by an optional body that has more details about the
  changes. Refer to [this link](https://github.com/erlang/otp/wiki/writing-good-commit-messages)
  for more information on writing good commit messages.

- Don't add a period/dot (.) at the end of the summary line.
