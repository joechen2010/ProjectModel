
/****** Object:  Default [DF__APPL_CONF__EDITA__09D45A2B]    Script Date: 07/12/2010 ******/
ALTER TABLE [CONFIG].[APPL_CONFIG] ADD  DEFAULT ((0)) FOR [EDITABLE_FLAG]


/****** Object:  ForeignKey [FK_APCO_APCC]    Script Date: 07/12/2010 ******/
ALTER TABLE [CONFIG].[APPL_CONFIG]  WITH NOCHECK ADD  CONSTRAINT [FK_APCO_APCC] FOREIGN KEY([APPL_CONFIG_CTGY_CODE])


ALTER TABLE [CONFIG].[APPL_CONFIG] CHECK CONSTRAINT [FK_APCO_APCC]



