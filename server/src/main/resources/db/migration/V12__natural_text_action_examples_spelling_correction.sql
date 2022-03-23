
update natural_text_action_examples set
example = REPLACE(example,'thie','this')
where example LIKE '%thie%';

update natural_text_action_examples set
example = REPLACE(example,'exmaple','example')
where example LIKE '%exmaple%';

update natural_text_action_examples set
example = REPLACE(example,'wil ltap','will tap')
where example LIKE '%wil ltap%';