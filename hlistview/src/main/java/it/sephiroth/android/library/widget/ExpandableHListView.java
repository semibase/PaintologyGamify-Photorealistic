/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.sephiroth.android.library.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ExpandableListAdapter;
import android.widget.ListAdapter;

import java.util.ArrayList;

import it.sephiroth.android.library.R;

/**
 * A view that shows items in a vertically scrolling two-level list. This
 * differs from the {@link HListView} by allowing two levels: groups which can
 * individually be expanded to show its children. The items come from the
 * {@link android.widget.ExpandableListAdapter} associated with this view.
 * <p/>
 * Expandable lists are able to show an indicator beside each item to display
 * the item's current state (the states are usually one of expanded group,
 * collapsed group, child, or last child). Use
 * {@link #setChildIndicator(android.graphics.drawable.Drawable)} or {@link #setGroupIndicator(android.graphics.drawable.Drawable)}
 * (or the corresponding XML attributes) to set these indicators (see the docs
 * for each method to see additional state that each Drawable can have). The
 * default style for an {@link it.sephiroth.android.library.widget.ExpandableHListView} provides indicators which
 * will be shown next to Views given to the {@link it.sephiroth.android.library.widget.ExpandableHListView}. The
 * layouts android.R.layout.simple_expandable_list_item_1 and
 * android.R.layout.simple_expandable_list_item_2 (which should be used with
 * {@link android.widget.SimpleCursorTreeAdapter}) contain the preferred position information
 * for indicators.
 * <p/>
 * The context menu information set by an {@link it.sephiroth.android.library.widget.ExpandableHListView} will be a
 * {@link it.sephiroth.android.library.widget.ExpandableHListView.ExpandableListContextMenuInfo} object with
 * {@link it.sephiroth.android.library.widget.ExpandableHListView.ExpandableListContextMenuInfo#packedPosition} being a packed position
 * that can be used with {@link #getPackedPositionType(long)} and the other
 * similar methods.
 * <p/>
 * <em><b>Note:</b></em> You cannot use the value <code>wrap_content</code>
 * for the <code>android:layout_height</code> attribute of a
 * ExpandableListView in XML if the parent's size is also not strictly specified
 * (for example, if the parent were ScrollView you could not specify
 * wrap_content since it also can be any length. However, you can use
 * wrap_content if the ExpandableListView parent has a specific size, such as
 * 100 pixels.
 *
 * @attr ref android.R.styleable#ExpandableListView_groupIndicator
 * @attr ref android.R.styleable#ExpandableListView_indicatorLeft
 * @attr ref android.R.styleable#ExpandableListView_indicatorRight
 * @attr ref android.R.styleable#ExpandableListView_childIndicator
 * @attr ref android.R.styleable#ExpandableListView_childIndicatorLeft
 * @attr ref android.R.styleable#ExpandableListView_childIndicatorRight
 * @attr ref android.R.styleable#ExpandableListView_childDivider
 * @attr ref android.R.styleable#ExpandableListView_indicatorStart
 * @attr ref android.R.styleable#ExpandableListView_indicatorEnd
 * @attr ref android.R.styleable#ExpandableListView_childIndicatorStart
 * @attr ref android.R.styleable#ExpandableListView_childIndicatorEnd
 */
public class ExpandableHListView extends HListView {

    /**
     * The packed position represents a group.
     */
    public static final int PACKED_POSITION_TYPE_GROUP = 0;

    /**
     * The packed position represents a child.
     */
    public static final int PACKED_POSITION_TYPE_CHILD = 1;

    /**
     * The packed position represents a neither/null/no preference.
     */
    public static final int PACKED_POSITION_TYPE_NULL = 2;

    /**
     * The value for a packed position that represents neither/null/no
     * preference. This value is not otherwise possible since a group type
     * (first bit 0) should not have a child position filled.
     */
    public static final long PACKED_POSITION_VALUE_NULL = 0x00000000FFFFFFFFL;

    /**
     * The mask (in packed position representation) for the child
     */
    private static final long PACKED_POSITION_MASK_CHILD = 0x00000000FFFFFFFFL;

    /**
     * The mask (in packed position representation) for the group
     */
    private static final long PACKED_POSITION_MASK_GROUP = 0x7FFFFFFF00000000L;

    /**
     * The mask (in packed position representation) for the type
     */
    private static final long PACKED_POSITION_MASK_TYPE = 0x8000000000000000L;

    /**
     * The shift amount (in packed position representation) for the group
     */
    private static final long PACKED_POSITION_SHIFT_GROUP = 32;

    /**
     * The shift amount (in packed position representation) for the type
     */
    private static final long PACKED_POSITION_SHIFT_TYPE = 63;

    /**
     * The mask (in integer child position representation) for the child
     */
    private static final long PACKED_POSITION_INT_MASK_CHILD = 0xFFFFFFFF;

    /**
     * The mask (in integer group position representation) for the group
     */
    private static final long PACKED_POSITION_INT_MASK_GROUP = 0x7FFFFFFF;

    /**
     * Serves as the glue/translator between a ListView and an ExpandableListView
     */
    private ExpandableHListConnector mConnector;

    /**
     * Gives us Views through group+child positions
     */
    private ExpandableListAdapter mAdapter;

    /**
     * Left bound for drawing the indicator.
     */
    private int mIndicatorTop;

    /**
     * Right bound for drawing the indicator.
     */
    private int mIndicatorLeft;

    /**
     * Group indicator gravity
     */
    private int mIndicatorGravity;

    /**
     * Child indicator gravity
     */
    private int mChildIndicatorGravity;

    private int mChildIndicatorTop;

    private int mChildIndicatorLeft;

    /**
     * Denotes when a child indicator should inherit this bound from the generic
     * indicator bounds
     */
    public static final int CHILD_INDICATOR_INHERIT = -1;

    /**
     * Denotes an undefined value for an indicator
     */
    private static final int INDICATOR_UNDEFINED = -2;

    /**
     * The indicator drawn next to a group.
     */
    private Drawable mGroupIndicator;

    /**
     * The indicator drawn next to a child.
     */
    private Drawable mChildIndicator;

    private static final int[] EMPTY_STATE_SET = {};

    /**
     * State indicating the group is expanded.
     */
    private static final int[] GROUP_EXPANDED_STATE_SET = {android.R.attr.state_expanded};

    /**
     * State indicating the group is empty (has no children).
     */
    private static final int[] GROUP_EMPTY_STATE_SET = {android.R.attr.state_empty};

    /**
     * State indicating the group is expanded and empty (has no children).
     */
    private static final int[] GROUP_EXPANDED_EMPTY_STATE_SET = {android.R.attr.state_expanded, android.R.attr.state_empty};

    /**
     * States for the group where the 0th bit is expanded and 1st bit is empty.
     */
    private static final int[][] GROUP_STATE_SETS = {EMPTY_STATE_SET, // 00
            GROUP_EXPANDED_STATE_SET, // 01
            GROUP_EMPTY_STATE_SET, // 10
            GROUP_EXPANDED_EMPTY_STATE_SET // 11
    };

    /**
     * State indicating the child is the last within its group.
     */
    private static final int[] CHILD_LAST_STATE_SET = {android.R.attr.state_last};

    /**
     * Drawable to be used as a divider when it is adjacent to any children
     */
    private Drawable mChildDivider;

    // Bounds of the indicator to be drawn
    private final Rect mIndicatorRect = new Rect();

    private final Rect mTempRect = new Rect();

    private int mGroupIndicatorWidth;
    private int mGroupIndicatorHeight;
    private int mChildIndicatorWidth;
    private int mChildIndicatorHeight;

    public ExpandableHListView(Context context) {
        this(context, null);
    }

    public ExpandableHListView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.hlv_expandableListViewStyle);
    }

    public ExpandableHListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExpandableHListView, defStyle, 0);

        setGroupIndicator(a.getDrawable(R.styleable.ExpandableHListView_hlv_groupIndicator));
        setChildIndicator(a.getDrawable(R.styleable.ExpandableHListView_hlv_childIndicator));

        mIndicatorLeft = a.getDimensionPixelSize(R.styleable.ExpandableHListView_hlv_indicatorPaddingLeft, 0);
        mIndicatorTop = a.getDimensionPixelSize(R.styleable.ExpandableHListView_hlv_indicatorPaddingTop, 0);

        mIndicatorGravity = a.getInt(R.styleable.ExpandableHListView_hlv_indicatorGravity, Gravity.NO_GRAVITY);
        mChildIndicatorGravity = a.getInt(R.styleable.ExpandableHListView_hlv_childIndicatorGravity, Gravity.NO_GRAVITY);

        mChildIndicatorLeft = a.getDimensionPixelSize(R.styleable.ExpandableHListView_hlv_childIndicatorPaddingLeft, 0);
        mChildIndicatorTop = a.getDimensionPixelSize(R.styleable.ExpandableHListView_hlv_childIndicatorPaddingTop, 0);

        mChildDivider = a.getDrawable(R.styleable.ExpandableHListView_hlv_childDivider);
        a.recycle();
    }

    public void onRtlPropertiesChanged(int layoutDirection) {
        resolveIndicator();
        resolveChildIndicator();
    }

    /**
     * Resolve start/end indicator. start/end indicator always takes precedence over left/right
     * indicator when defined.
     */
    private void resolveIndicator() {
        if (null != mGroupIndicator) {
            mGroupIndicatorWidth = mGroupIndicator.getIntrinsicWidth();
            mGroupIndicatorHeight = mGroupIndicator.getIntrinsicHeight();
        } else {
            mGroupIndicatorWidth = 0;
            mGroupIndicatorHeight = 0;
        }
    }

    /**
     * Resolve start/end child indicator. start/end child indicator always takes precedence over
     * left/right child indicator when defined.
     */
    private void resolveChildIndicator() {
        if (null != mChildIndicator) {
            mChildIndicatorWidth = mChildIndicator.getIntrinsicWidth();
            mChildIndicatorHeight = mChildIndicator.getIntrinsicHeight();
        } else {
            mChildIndicatorWidth = 0;
            mChildIndicatorHeight = 0;
        }
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        // Draw children, etc.
        super.dispatchDraw(canvas);

        // If we have any indicators to draw, we do it here
        if ((mChildIndicator == null) && (mGroupIndicator == null)) {
            return;
        }

        int saveCount = 0;

        final int headerViewsCount = getHeaderViewsCount();

        final int lastChildFlPos = mItemCount - getFooterViewsCount() - headerViewsCount - 1;

        final int myRight = getRight();

        ExpandableHListConnector.PositionMetadata pos;
        View item;
        Drawable indicator;
        int left, right;

        // Start at a value that is neither child nor group
        int lastItemType = ~(ExpandableHListPosition.CHILD | ExpandableHListPosition.GROUP);

        final Rect indicatorRect = mIndicatorRect;

        // The "child" mentioned in the following two lines is this
        // View's child, not referring to an expandable list's
        // notion of a child (as opposed to a group)
        final int childCount = getChildCount();
        for (int i = 0, childFlPos = mFirstPosition - headerViewsCount; i < childCount; i++, childFlPos++) {

            if (childFlPos < 0) {
                // This child is header
                continue;
            } else if (childFlPos > lastChildFlPos) {
                // This child is footer, so are all subsequent children
                break;
            }

            item = getChildAt(i);
            left = item.getLeft();
            right = item.getRight();

            // This item isn't on the screen
            if ((right < 0) || (left > myRight)) continue;

            // Get more expandable list-related info for this item
            pos = mConnector.getUnflattenedPos(childFlPos);

            // If this item type and the previous item type are different, then we need to change
            // the left & right bounds
            if (pos.position.type != lastItemType) {
                if (pos.position.type == ExpandableHListPosition.CHILD) {
                    indicatorRect.top = item.getTop() + mChildIndicatorTop;
                    indicatorRect.bottom = item.getBottom() + mChildIndicatorTop;
                } else {
                    indicatorRect.top = item.getTop() + mIndicatorTop;
                    indicatorRect.bottom = item.getBottom() + mIndicatorTop;
                }

                lastItemType = pos.position.type;
            }

            if (indicatorRect.top != indicatorRect.bottom) {
                if (pos.position.type == ExpandableHListPosition.CHILD) {
                    indicatorRect.left = left + mChildIndicatorLeft;
                    indicatorRect.right = right + mChildIndicatorLeft;
                } else {
                    indicatorRect.left = left + mIndicatorLeft;
                    indicatorRect.right = right + mIndicatorLeft;
                }


                // Get the indicator (with its state set to the item's state)
                indicator = getIndicator(pos);
                if (indicator != null) {

                    if (pos.position.type == ExpandableHListPosition.CHILD) {
                        Gravity.apply(mChildIndicatorGravity, mChildIndicatorWidth, mChildIndicatorHeight, indicatorRect, mTempRect);
                    } else {
                        Gravity.apply(mIndicatorGravity, mGroupIndicatorWidth, mGroupIndicatorHeight, indicatorRect, mTempRect);
                    }

                    // Draw the indicator
                    indicator.setBounds(mTempRect);
                    indicator.draw(canvas);
                }
            }
            pos.recycle();
        }
    }

    /**
     * Gets the indicator for the item at the given position. If the indicator
     * is stateful, the state will be given to the indicator.
     *
     * @param pos The flat list position of the item whose indicator
     *            should be returned.
     * @return The indicator in the proper state.
     */
    private Drawable getIndicator(ExpandableHListConnector.PositionMetadata pos) {
        Drawable indicator;

        if (pos.position.type == ExpandableHListPosition.GROUP) {
            indicator = mGroupIndicator;

            if (indicator != null && indicator.isStateful()) {
                // Empty check based on availability of data.  If the groupMetadata isn't null,
                // we do a check on it. Otherwise, the group is collapsed so we consider it
                // empty for performance reasons.
                boolean isEmpty = (pos.groupMetadata == null) || (pos.groupMetadata.lastChildFlPos == pos.groupMetadata.flPos);

                final int stateSetIndex = (pos.isExpanded() ? 1 : 0) | // Expanded?
                        (isEmpty ? 2 : 0); // Empty?
                indicator.setState(GROUP_STATE_SETS[stateSetIndex]);
            }
        } else {
            indicator = mChildIndicator;

            if (indicator != null && indicator.isStateful()) {
                // No need for a state sets array for the child since it only has two states
                final int stateSet[] = pos.position.flatListPos == pos.groupMetadata.lastChildFlPos ? CHILD_LAST_STATE_SET : EMPTY_STATE_SET;
                indicator.setState(stateSet);
            }
        }

        return indicator;
    }

    /**
     * Sets the drawable that will be drawn adjacent to every child in the list. This will
     * be drawn using the same height as the normal divider ({@link #setDivider(android.graphics.drawable.Drawable)}) or
     * if it does not have an intrinsic height, the height set by {@link #setDividerWidth(int)}.
     *
     * @param childDivider The drawable to use.
     */
    public void setChildDivider(Drawable childDivider) {
        mChildDivider = childDivider;
    }

    @Override
    void drawDivider(Canvas canvas, Rect bounds, int childIndex) {
        int flatListPosition = childIndex + mFirstPosition;

        // Only proceed as possible child if the divider isn't above all items (if it is above
        // all items, then the item below it has to be a group)
        if (flatListPosition >= 0) {
            final int adjustedPosition = getFlatPositionForConnector(flatListPosition);
            ExpandableHListConnector.PositionMetadata pos = mConnector.getUnflattenedPos(adjustedPosition);
            // If this item is a child, or it is a non-empty group that is expanded
            if ((pos.position.type == ExpandableHListPosition.CHILD) || (pos.isExpanded() && pos.groupMetadata.lastChildFlPos != pos.groupMetadata.flPos)) {
                // These are the cases where we draw the child divider
                final Drawable divider = mChildDivider;
                divider.setBounds(bounds);
                divider.draw(canvas);
                pos.recycle();
                return;
            }
            pos.recycle();
        }

        // Otherwise draw the default divider
        super.drawDivider(canvas, bounds, flatListPosition);
    }

    /**
     * This overloaded method should not be used, instead use
     * {@link #setAdapter(android.widget.ExpandableListAdapter)}.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public void setAdapter(ListAdapter adapter) {
        throw new RuntimeException("For ExpandableListView, use setAdapter(ExpandableListAdapter) instead of " + "setAdapter(ListAdapter)");
    }

    /**
     * This method should not be used, use {@link #getExpandableListAdapter()}.
     */
    @Override
    public ListAdapter getAdapter() {
        /*
         * The developer should never really call this method on an
         * ExpandableListView, so it would be nice to throw a RuntimeException,
         * but AdapterView calls this
         */
        return super.getAdapter();
    }

    /**
     * Register a callback to be invoked when an item has been clicked and the
     * caller prefers to receive a ListView-style position instead of a group
     * and/or child position. In most cases, the caller should use
     * {@link #setOnGroupClickListener} and/or {@link #setOnChildClickListener}.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public void setOnItemClickListener(AdapterView.OnItemClickListener l) {
        super.setOnItemClickListener(l);
    }

    /**
     * Sets the adapter that provides data to this view.
     *
     * @param adapter The adapter that provides data to this view.
     */
    public void setAdapter(ExpandableListAdapter adapter) {
        // Set member variable
        mAdapter = adapter;

        if (adapter != null) {
            // Create the connector
            mConnector = new ExpandableHListConnector(adapter);
        } else {
            mConnector = null;
        }

        // Link the ListView (superclass) to the expandable list data through the connector
        super.setAdapter(mConnector);
    }

    /**
     * Gets the adapter that provides data to this view.
     *
     * @return The adapter that provides data to this view.
     */
    public ExpandableListAdapter getExpandableListAdapter() {
        return mAdapter;
    }

    /**
     * @param position An absolute (including header and footer) flat list position.
     * @return true if the position corresponds to a header or a footer item.
     */
    private boolean isHeaderOrFooterPosition(int position) {
        final int footerViewsStart = mItemCount - getFooterViewsCount();
        return (position < getHeaderViewsCount() || position >= footerViewsStart);
    }

    /**
     * Converts an absolute item flat position into a group/child flat position, shifting according
     * to the number of header items.
     *
     * @param flatListPosition The absolute flat position
     * @return A group/child flat position as expected by the connector.
     */
    private int getFlatPositionForConnector(int flatListPosition) {
        return flatListPosition - getHeaderViewsCount();
    }

    /**
     * Converts a group/child flat position into an absolute flat position, that takes into account
     * the possible headers.
     *
     * @param flatListPosition The child/group flat position
     * @return An absolute flat position.
     */
    private int getAbsoluteFlatPosition(int flatListPosition) {
        return flatListPosition + getHeaderViewsCount();
    }

    @Override
    public boolean performItemClick(View v, int position, long id) {
        // Ignore clicks in header/footers
        if (isHeaderOrFooterPosition(position)) {
            // Clicked on a header/footer, so ignore pass it on to super
            return super.performItemClick(v, position, id);
        }

        // Internally handle the item click
        final int adjustedPosition = getFlatPositionForConnector(position);
        return handleItemClick(v, adjustedPosition, id);
    }

    /**
     * This will either expand/collapse groups (if a group was clicked) or pass
     * on the click to the proper child (if a child was clicked)
     *
     * @param position The flat list position. This has already been factored to
     *                 remove the header/footer.
     * @param id       The ListAdapter ID, not the group or child ID.
     */
    boolean handleItemClick(View v, int position, long id) {
        final ExpandableHListConnector.PositionMetadata posMetadata = mConnector.getUnflattenedPos(position);

        id = getChildOrGroupId(posMetadata.position);

        boolean returnValue;
        if (posMetadata.position.type == ExpandableHListPosition.GROUP) {
            /* It's a group, so handle collapsing/expanding */

            /* It's a group click, so pass on event */
            if (mOnGroupClickListener != null) {
                if (mOnGroupClickListener.onGroupClick(this, v, posMetadata.position.groupPos, id)) {
                    posMetadata.recycle();
                    return true;
                }
            }

            if (posMetadata.isExpanded()) {
                /* Collapse it */
                mConnector.collapseGroup(posMetadata);

                playSoundEffect(SoundEffectConstants.CLICK);

                if (mOnGroupCollapseListener != null) {
                    mOnGroupCollapseListener.onGroupCollapse(posMetadata.position.groupPos);
                }
            } else {
                /* Expand it */
                mConnector.expandGroup(posMetadata);

                playSoundEffect(SoundEffectConstants.CLICK);

                if (mOnGroupExpandListener != null) {
                    mOnGroupExpandListener.onGroupExpand(posMetadata.position.groupPos);
                }

                final int groupPos = posMetadata.position.groupPos;
                final int groupFlatPos = posMetadata.position.flatListPos;

                final int shiftedGroupPosition = groupFlatPos + getHeaderViewsCount();
                smoothScrollToPosition(shiftedGroupPosition + mAdapter.getChildrenCount(groupPos), shiftedGroupPosition);
            }

            returnValue = true;
        } else {
            /* It's a child, so pass on event */
            if (mOnChildClickListener != null) {
                playSoundEffect(SoundEffectConstants.CLICK);
                return mOnChildClickListener.onChildClick(this, v, posMetadata.position.groupPos, posMetadata.position.childPos, id);
            }

            returnValue = false;
        }

        posMetadata.recycle();

        return returnValue;
    }

    /**
     * Expand a group in the grouped list view
     *
     * @param groupPos the group to be expanded
     * @return True if the group was expanded, false otherwise (if the group
     * was already expanded, this will return false)
     */
    public boolean expandGroup(int groupPos) {
        return expandGroup(groupPos, false);
    }

    /**
     * Expand a group in the grouped list view
     *
     * @param groupPos the group to be expanded
     * @param animate  true if the expanding group should be animated in
     * @return True if the group was expanded, false otherwise (if the group
     * was already expanded, this will return false)
     */
    public boolean expandGroup(int groupPos, boolean animate) {
        ExpandableHListPosition elGroupPos = ExpandableHListPosition.obtain(ExpandableHListPosition.GROUP, groupPos, -1, -1);
        ExpandableHListConnector.PositionMetadata pm = mConnector.getFlattenedPos(elGroupPos);
        elGroupPos.recycle();
        boolean retValue = mConnector.expandGroup(pm);

        if (mOnGroupExpandListener != null) {
            mOnGroupExpandListener.onGroupExpand(groupPos);
        }

        if (animate) {
            final int groupFlatPos = pm.position.flatListPos;

            final int shiftedGroupPosition = groupFlatPos + getHeaderViewsCount();
            smoothScrollToPosition(shiftedGroupPosition + mAdapter.getChildrenCount(groupPos), shiftedGroupPosition);
        }
        pm.recycle();

        return retValue;
    }

    /**
     * Collapse a group in the grouped list view
     *
     * @param groupPos position of the group to collapse
     * @return True if the group was collapsed, false otherwise (if the group
     * was already collapsed, this will return false)
     */
    public boolean collapseGroup(int groupPos) {
        boolean retValue = mConnector.collapseGroup(groupPos);

        if (mOnGroupCollapseListener != null) {
            mOnGroupCollapseListener.onGroupCollapse(groupPos);
        }

        return retValue;
    }

    /**
     * Used for being notified when a group is collapsed
     */
    public interface OnGroupCollapseListener {
        /**
         * Callback method to be invoked when a group in this expandable list has
         * been collapsed.
         *
         * @param groupPosition The group position that was collapsed
         */
        void onGroupCollapse(int groupPosition);
    }

    private OnGroupCollapseListener mOnGroupCollapseListener;

    public void setOnGroupCollapseListener(
            OnGroupCollapseListener onGroupCollapseListener) {
        mOnGroupCollapseListener = onGroupCollapseListener;
    }

    /**
     * Used for being notified when a group is expanded
     */
    public interface OnGroupExpandListener {
        /**
         * Callback method to be invoked when a group in this expandable list has
         * been expanded.
         *
         * @param groupPosition The group position that was expanded
         */
        void onGroupExpand(int groupPosition);
    }

    private OnGroupExpandListener mOnGroupExpandListener;

    public void setOnGroupExpandListener(
            OnGroupExpandListener onGroupExpandListener) {
        mOnGroupExpandListener = onGroupExpandListener;
    }

    /**
     * Interface definition for a callback to be invoked when a group in this
     * expandable list has been clicked.
     */
    public interface OnGroupClickListener {
        /**
         * Callback method to be invoked when a group in this expandable list has
         * been clicked.
         *
         * @param parent        The ExpandableHListConnector where the click happened
         * @param v             The view within the expandable list/ListView that was clicked
         * @param groupPosition The group position that was clicked
         * @param id            The row id of the group that was clicked
         * @return True if the click was handled
         */
        boolean onGroupClick(
                ExpandableHListView parent, View v, int groupPosition, long id);
    }

    private OnGroupClickListener mOnGroupClickListener;

    public void setOnGroupClickListener(OnGroupClickListener onGroupClickListener) {
        mOnGroupClickListener = onGroupClickListener;
    }

    /**
     * Interface definition for a callback to be invoked when a child in this
     * expandable list has been clicked.
     */
    public interface OnChildClickListener {
        /**
         * Callback method to be invoked when a child in this expandable list has
         * been clicked.
         *
         * @param parent        The ExpandableListView where the click happened
         * @param v             The view within the expandable list/ListView that was clicked
         * @param groupPosition The group position that contains the child that
         *                      was clicked
         * @param childPosition The child position within the group
         * @param id            The row id of the child that was clicked
         * @return True if the click was handled
         */
        boolean onChildClick(
                ExpandableHListView parent, View v, int groupPosition, int childPosition, long id);
    }

    private OnChildClickListener mOnChildClickListener;

    public void setOnChildClickListener(OnChildClickListener onChildClickListener) {
        mOnChildClickListener = onChildClickListener;
    }

    /**
     * Converts a flat list position (the raw position of an item (child or group)
     * in the list) to a group and/or child position (represented in a
     * packed position). This is useful in situations where the caller needs to
     * use the underlying {@link android.widget.ListView}'s methods. Use
     * {@link it.sephiroth.android.library.widget.ExpandableHListView#getPackedPositionType} ,
     * {@link it.sephiroth.android.library.widget.ExpandableHListView#getPackedPositionChild},
     * {@link it.sephiroth.android.library.widget.ExpandableHListView#getPackedPositionGroup} to unpack.
     *
     * @param flatListPosition The flat list position to be converted.
     * @return The group and/or child position for the given flat list position
     * in packed position representation. #PACKED_POSITION_VALUE_NULL if
     * the position corresponds to a header or a footer item.
     */
    public long getExpandableListPosition(int flatListPosition) {
        if (isHeaderOrFooterPosition(flatListPosition)) {
            return PACKED_POSITION_VALUE_NULL;
        }

        final int adjustedPosition = getFlatPositionForConnector(flatListPosition);
        ExpandableHListConnector.PositionMetadata pm = mConnector.getUnflattenedPos(adjustedPosition);
        long packedPos = pm.position.getPackedPosition();
        pm.recycle();
        return packedPos;
    }

    /**
     * Converts a group and/or child position to a flat list position. This is
     * useful in situations where the caller needs to use the underlying
     * {@link android.widget.ListView}'s methods.
     *
     * @param packedPosition The group and/or child positions to be converted in
     *                       packed position representation. Use
     *                       {@link #getPackedPositionForChild(int, int)} or
     *                       {@link #getPackedPositionForGroup(int)}.
     * @return The flat list position for the given child or group.
     */
    public int getFlatListPosition(long packedPosition) {
        ExpandableHListPosition elPackedPos = ExpandableHListPosition.obtainPosition(packedPosition);
        ExpandableHListConnector.PositionMetadata pm = mConnector.getFlattenedPos(elPackedPos);
        elPackedPos.recycle();
        final int flatListPosition = pm.position.flatListPos;
        pm.recycle();
        return getAbsoluteFlatPosition(flatListPosition);
    }

    /**
     * Gets the position of the currently selected group or child (along with
     * its type). Can return {@link #PACKED_POSITION_VALUE_NULL} if no selection.
     *
     * @return A packed position containing the currently selected group or
     * child's position and type. #PACKED_POSITION_VALUE_NULL if no selection
     * or if selection is on a header or a footer item.
     */
    public long getSelectedPosition() {
        final int selectedPos = getSelectedItemPosition();

        // The case where there is no selection (selectedPos == -1) is also handled here.
        return getExpandableListPosition(selectedPos);
    }

    /**
     * Gets the ID of the currently selected group or child. Can return -1 if no
     * selection.
     *
     * @return The ID of the currently selected group or child. -1 if no
     * selection.
     */
    public long getSelectedId() {
        long packedPos = getSelectedPosition();
        if (packedPos == PACKED_POSITION_VALUE_NULL) return -1;

        int groupPos = getPackedPositionGroup(packedPos);

        if (getPackedPositionType(packedPos) == PACKED_POSITION_TYPE_GROUP) {
            // It's a group
            return mAdapter.getGroupId(groupPos);
        } else {
            // It's a child
            return mAdapter.getChildId(groupPos, getPackedPositionChild(packedPos));
        }
    }

    /**
     * Sets the selection to the specified group.
     *
     * @param groupPosition The position of the group that should be selected.
     */
    public void setSelectedGroup(int groupPosition) {
        ExpandableHListPosition elGroupPos = ExpandableHListPosition.obtainGroupPosition(groupPosition);
        ExpandableHListConnector.PositionMetadata pm = mConnector.getFlattenedPos(elGroupPos);
        elGroupPos.recycle();
        final int absoluteFlatPosition = getAbsoluteFlatPosition(pm.position.flatListPos);
        super.setSelection(absoluteFlatPosition);
        pm.recycle();
    }

    /**
     * Sets the selection to the specified child. If the child is in a collapsed
     * group, the group will only be expanded and child subsequently selected if
     * shouldExpandGroup is set to true, otherwise the method will return false.
     *
     * @param groupPosition     The position of the group that contains the child.
     * @param childPosition     The position of the child within the group.
     * @param shouldExpandGroup Whether the child's group should be expanded if
     *                          it is collapsed.
     * @return Whether the selection was successfully set on the child.
     */
    public boolean setSelectedChild(int groupPosition, int childPosition, boolean shouldExpandGroup) {
        ExpandableHListPosition elChildPos = ExpandableHListPosition.obtainChildPosition(groupPosition, childPosition);
        ExpandableHListConnector.PositionMetadata flatChildPos = mConnector.getFlattenedPos(elChildPos);

        if (flatChildPos == null) {
            // The child's group isn't expanded

            // Shouldn't expand the group, so return false for we didn't set the selection
            if (!shouldExpandGroup) return false;

            expandGroup(groupPosition);

            flatChildPos = mConnector.getFlattenedPos(elChildPos);

            // Sanity check
            if (flatChildPos == null) {
                throw new IllegalStateException("Could not find child");
            }
        }

        int absoluteFlatPosition = getAbsoluteFlatPosition(flatChildPos.position.flatListPos);
        super.setSelection(absoluteFlatPosition);

        elChildPos.recycle();
        flatChildPos.recycle();

        return true;
    }

    /**
     * Whether the given group is currently expanded.
     *
     * @param groupPosition The group to check.
     * @return Whether the group is currently expanded.
     */
    public boolean isGroupExpanded(int groupPosition) {
        return mConnector.isGroupExpanded(groupPosition);
    }

    /**
     * Gets the type of a packed position. See
     * {@link #getPackedPositionForChild(int, int)}.
     *
     * @param packedPosition The packed position for which to return the type.
     * @return The type of the position contained within the packed position,
     * either {@link #PACKED_POSITION_TYPE_CHILD}, {@link #PACKED_POSITION_TYPE_GROUP}, or
     * {@link #PACKED_POSITION_TYPE_NULL}.
     */
    public static int getPackedPositionType(long packedPosition) {
        if (packedPosition == PACKED_POSITION_VALUE_NULL) {
            return PACKED_POSITION_TYPE_NULL;
        }

        return (packedPosition & PACKED_POSITION_MASK_TYPE) == PACKED_POSITION_MASK_TYPE ? PACKED_POSITION_TYPE_CHILD : PACKED_POSITION_TYPE_GROUP;
    }

    /**
     * Gets the group position from a packed position. See
     * {@link #getPackedPositionForChild(int, int)}.
     *
     * @param packedPosition The packed position from which the group position
     *                       will be returned.
     * @return The group position portion of the packed position. If this does
     * not contain a group, returns -1.
     */
    public static int getPackedPositionGroup(long packedPosition) {
        // Null
        if (packedPosition == PACKED_POSITION_VALUE_NULL) return -1;

        return (int) ((packedPosition & PACKED_POSITION_MASK_GROUP) >> PACKED_POSITION_SHIFT_GROUP);
    }

    /**
     * Gets the child position from a packed position that is of
     * {@link #PACKED_POSITION_TYPE_CHILD} type (use {@link #getPackedPositionType(long)}).
     * To get the group that this child belongs to, use
     * {@link #getPackedPositionGroup(long)}. See
     * {@link #getPackedPositionForChild(int, int)}.
     *
     * @param packedPosition The packed position from which the child position
     *                       will be returned.
     * @return The child position portion of the packed position. If this does
     * not contain a child, returns -1.
     */
    public static int getPackedPositionChild(long packedPosition) {
        // Null
        if (packedPosition == PACKED_POSITION_VALUE_NULL) return -1;

        // Group since a group type clears this bit
        if ((packedPosition & PACKED_POSITION_MASK_TYPE) != PACKED_POSITION_MASK_TYPE) return -1;

        return (int) (packedPosition & PACKED_POSITION_MASK_CHILD);
    }

    /**
     * Returns the packed position representation of a child's position.
     * <p/>
     * In general, a packed position should be used in
     * situations where the position given to/returned from an
     * {@link android.widget.ExpandableListAdapter} or {@link it.sephiroth.android.library.widget.ExpandableHListView} method can
     * either be a child or group. The two positions are packed into a single
     * long which can be unpacked using
     * {@link #getPackedPositionChild(long)},
     * {@link #getPackedPositionGroup(long)}, and
     * {@link #getPackedPositionType(long)}.
     *
     * @param groupPosition The child's parent group's position.
     * @param childPosition The child position within the group.
     * @return The packed position representation of the child (and parent group).
     */
    public static long getPackedPositionForChild(int groupPosition, int childPosition) {
        return (((long) PACKED_POSITION_TYPE_CHILD) << PACKED_POSITION_SHIFT_TYPE) |
                ((((long) groupPosition) & PACKED_POSITION_INT_MASK_GROUP) << PACKED_POSITION_SHIFT_GROUP) | (childPosition & PACKED_POSITION_INT_MASK_CHILD);
    }

    /**
     * Returns the packed position representation of a group's position. See
     * {@link #getPackedPositionForChild(int, int)}.
     *
     * @param groupPosition The child's parent group's position.
     * @return The packed position representation of the group.
     */
    public static long getPackedPositionForGroup(int groupPosition) {
        // No need to OR a type in because PACKED_POSITION_GROUP == 0
        return ((((long) groupPosition) & PACKED_POSITION_INT_MASK_GROUP) << PACKED_POSITION_SHIFT_GROUP);
    }

    @Override
    ContextMenuInfo createContextMenuInfo(View view, int flatListPosition, long id) {
        if (isHeaderOrFooterPosition(flatListPosition)) {
            // Return normal info for header/footer view context menus
            return new AdapterView.AdapterContextMenuInfo(view, flatListPosition, id);
        }

        final int adjustedPosition = getFlatPositionForConnector(flatListPosition);
        ExpandableHListConnector.PositionMetadata pm = mConnector.getUnflattenedPos(adjustedPosition);
        ExpandableHListPosition pos = pm.position;

        id = getChildOrGroupId(pos);
        long packedPosition = pos.getPackedPosition();

        pm.recycle();

        return new ExpandableListContextMenuInfo(view, packedPosition, id);
    }

    /**
     * Gets the ID of the group or child at the given <code>position</code>.
     * This is useful since there is no ListAdapter ID -> ExpandableListAdapter
     * ID conversion mechanism (in some cases, it isn't possible).
     *
     * @param position The position of the child or group whose ID should be
     *                 returned.
     */
    private long getChildOrGroupId(ExpandableHListPosition position) {
        if (position.type == ExpandableHListPosition.CHILD) {
            return mAdapter.getChildId(position.groupPos, position.childPos);
        } else {
            return mAdapter.getGroupId(position.groupPos);
        }
    }

    /**
     * Sets the indicator to be drawn next to a child.
     *
     * @param childIndicator The drawable to be used as an indicator. If the
     *                       child is the last child for a group, the state
     *                       {@link android.R.attr#state_last} will be set.
     */
    public void setChildIndicator(Drawable childIndicator) {
        mChildIndicator = childIndicator;
        resolveChildIndicator();
    }

    /**
     * Sets the indicator to be drawn next to a group.
     *
     * @param groupIndicator The drawable to be used as an indicator. If the
     *                       group is empty, the state {@link android.R.attr#state_empty} will be
     *                       set. If the group is expanded, the state
     *                       {@link android.R.attr#state_expanded} will be set.
     */
    public void setGroupIndicator(Drawable groupIndicator) {
        mGroupIndicator = groupIndicator;
        resolveIndicator();
    }

    /**
     * Extra menu information specific to an {@link it.sephiroth.android.library.widget.ExpandableHListView} provided
     * to the
     * {@link android.view.View.OnCreateContextMenuListener#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo) }
     * callback when a context menu is brought up for this AdapterView.
     */
    public static class ExpandableListContextMenuInfo implements ContextMenuInfo {

        public ExpandableListContextMenuInfo(View targetView, long packedPosition, long id) {
            this.targetView = targetView;
            this.packedPosition = packedPosition;
            this.id = id;
        }

        /**
         * The view for which the context menu is being displayed. This
         * will be one of the children Views of this {@link it.sephiroth.android.library.widget.ExpandableHListView}.
         */
        public View targetView;

        /**
         * The packed position in the list represented by the adapter for which
         * the context menu is being displayed. Use the methods
         * {@link it.sephiroth.android.library.widget.ExpandableHListView#getPackedPositionType},
         * {@link it.sephiroth.android.library.widget.ExpandableHListView#getPackedPositionChild}, and
         * {@link it.sephiroth.android.library.widget.ExpandableHListView#getPackedPositionGroup} to unpack this.
         */
        public long packedPosition;

        /**
         * The ID of the item (group or child) for which the context menu is
         * being displayed.
         */
        public long id;
    }

    static class SavedState extends View.BaseSavedState {
        ArrayList<ExpandableHListConnector.GroupMetadata> expandedGroupMetadataList;

        /**
         * Constructor called from {@link it.sephiroth.android.library.widget.ExpandableHListView#onSaveInstanceState()}
         */
        SavedState(
                Parcelable superState, ArrayList<ExpandableHListConnector.GroupMetadata> expandedGroupMetadataList) {
            super(superState);
            this.expandedGroupMetadataList = expandedGroupMetadataList;
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            expandedGroupMetadataList = new ArrayList<ExpandableHListConnector.GroupMetadata>();
            in.readList(expandedGroupMetadataList, ExpandableHListConnector.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeList(expandedGroupMetadataList);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, mConnector != null ? mConnector.getExpandedGroupMetadataList() : null);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        if (mConnector != null && ss.expandedGroupMetadataList != null) {
            mConnector.setExpandedGroupMetadataList(ss.expandedGroupMetadataList);
        }
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(ExpandableHListView.class.getName());
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(ExpandableHListView.class.getName());
    }
}

