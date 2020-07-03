SELECT avg(IF(`clone_pr_total`>0, `clone_used` /`clone_pr_total`,0)) p_ccfinder FROM `cc_ccfinder` WHERE `change_id` IN (GOOD_CHANGE_ID)
UNION
SELECT avg(IF(`clone_pr_total`>0, `clone_used` /`clone_pr_total`,0)) p_cloneworks_type1 FROM `cc_cloneworks_type1` WHERE `change_id` IN (GOOD_CHANGE_ID)
UNION
SELECT avg(IF(`clone_pr_total`>0, `clone_used` /`clone_pr_total`,0)) p_cloneworks_type2blind FROM `cc_cloneworks_type2blind` WHERE `change_id` IN (GOOD_CHANGE_ID)
UNION
SELECT avg(IF(`clone_pr_total`>0, `clone_used` /`clone_pr_total`,0)) p_cloneworks_type3pattern FROM `cc_cloneworks_type3pattern` WHERE `change_id` IN (GOOD_CHANGE_ID)
UNION
SELECT avg(IF(`clone_pr_total`>0, `clone_used` /`clone_pr_total`,0)) p_cloneworks_type3token FROM `cc_cloneworks_type3token` WHERE `change_id` IN (GOOD_CHANGE_ID)
UNION
SELECT avg(IF(`clone_pr_total`>0, `clone_used` /`clone_pr_total`,0)) p_conqat FROM `cc_conqat` WHERE `change_id` IN (GOOD_CHANGE_ID)
UNION
SELECT avg(IF(`clone_pr_total`>0, `clone_used` /`clone_pr_total`,0)) p_deckard_2_0 FROM `cc_deckard_2_0` WHERE `change_id` IN (GOOD_CHANGE_ID)
UNION
SELECT avg(IF(`clone_pr_total`>0, `clone_used` /`clone_pr_total`,0)) p_duplo FROM `cc_duplo` WHERE `change_id` IN (GOOD_CHANGE_ID)
UNION
SELECT avg(IF(`clone_pr_total`>0, `clone_used` /`clone_pr_total`,0)) p_iclones FROM `cc_iclones` WHERE `change_id` IN (GOOD_CHANGE_ID)
UNION
SELECT avg(IF(`clone_pr_total`>0, `clone_used` /`clone_pr_total`,0)) p_nicad5 FROM `cc_nicad5` WHERE `change_id` IN (GOOD_CHANGE_ID)
UNION
SELECT avg(IF(`clone_pr_total`>0, `clone_used` /`clone_pr_total`,0)) p_simcad FROM `cc_simcad` WHERE `change_id` IN (GOOD_CHANGE_ID)
UNION
SELECT avg(IF(`clone_pr_total`>0, `clone_used` /`clone_pr_total`,0)) p_simian FROM `cc_simian` WHERE `change_id` IN (GOOD_CHANGE_ID);