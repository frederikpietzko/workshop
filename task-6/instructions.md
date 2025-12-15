# Task 6 preparation

- create simple webapp with java and thymeleaf
- hibernate + spring data jpa
- only use thymeleaf and basic html
- style with plain css check style.css from task 1, 2, 3 and 4
- no htmx usage yet
- but include htmx libray in the tempalte
- setup uses h2, so no need for any database or migrations
- use hibernate validation where applicable
- render errors in the view as helper text
- create thymeleaf fragments for input elements and other components with helper texts and error message
- build setup includes everything you need
- check before you implement
- follow clean code principles
- create service layer
- Prefer a flat layout. no single class packages

# Application

- create a simple web application with multiple forms that throw errors
- create a multiple registration form, one throwing a 500 error, another registration form that works fine
- The form should use htmx to submit the forms
- errors should not be handeled (will be done by participants in the workshop)
- create another fragment for a toast notification that shows the error message, but do not use it yet
- also create a spinner in the form to show loading state (with htmx indicator)