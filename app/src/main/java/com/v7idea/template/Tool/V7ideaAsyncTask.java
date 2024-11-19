package com.v7idea.template.Tool;

import android.os.AsyncTask;

public abstract class V7ideaAsyncTask<Params, Progress> extends AsyncTask<Params, Progress, ApiResult>
{
	private final static String TAG = "V7ideaAsyncTask";

	public abstract void downLoadSuccess(ApiResult result);
	public abstract void downLoadFail(ApiResult apiResult);

	public void downLoadComplete()
	{

	}

	public void ifNeedCloseSomeThing()
	{

	}
	
	public boolean isActivityNoFinish()
	{
		return true;
	}
	
	@Override
	protected void onPostExecute(ApiResult result) {
		super.onPostExecute(result);
		
		if(isActivityNoFinish())
		{
			if(result != null)
			{
				DebugLog.d(TAG, result.toString());

				if(result.getErrorNo().contentEquals("200")
						|| result.getErrorNo().contentEquals("0000") || result.getErrorNo().contentEquals("2000")||result.getErrorNo().contentEquals("0"))
				{
					ifNeedCloseSomeThing();
					downLoadSuccess(result);
				}
				else
				{
					ifNeedCloseSomeThing();
					downLoadFail(result);
				}
			}
		}

		downLoadComplete();
	}
}
